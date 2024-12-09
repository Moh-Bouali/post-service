package com.individual_s7.post_service.service;

import com.individual_s7.post_service.configuration.RabbitMQConfig;
import com.individual_s7.post_service.dto.ContentModerationRequest;
import com.individual_s7.post_service.dto.ContentModerationResponse;
import com.individual_s7.post_service.dto.PostRequest;
import com.individual_s7.post_service.event.UserUpdatedEvent;
import com.individual_s7.post_service.model.IndividualPost;
import com.individual_s7.post_service.model.Post;
import com.individual_s7.post_service.model.PostContent;
import com.individual_s7.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserFriendsService userFriendsService;
    private final RestTemplate restTemplate;

    @Value("${azure.function.url}")
    private String contentModerationUrl;

    public void createOrUpdateUserPosts(PostRequest postRequest) {

        for (PostContent post : postRequest.content()) {
            if (post == null || post.getContent() == null || post.getContent().isEmpty()) {
                throw new IllegalArgumentException("Post content is required for moderation!");
            }
            if (!isContentAllowed(post.getContent())) {
                throw new IllegalArgumentException("Post content contains prohibited words!");
            }
        }

        // Find the user's existing posts by userId
        Optional<Post> existingPost = postRepository.findByUserId(postRequest.userId());

        if (existingPost.isPresent()) {
            // User exists, append the new post to the existing list of posts
            Post userPost = existingPost.get();
            userPost.getContent().addAll(postRequest.content()); // Add new posts to the existing list
            postRepository.save(userPost); // Save the updated post list
        } else {
            // User doesn't exist, create a new document
            Post newPost = Post.builder()
                    .userId(postRequest.userId())
                    .username(postRequest.username())
                    .content(postRequest.content())
                    .build();
            postRepository.save(newPost);
        }
    }

    private boolean isContentAllowed(String content) {
        try {
            // Send content to Azure Function
            ContentModerationRequest request = new ContentModerationRequest(content);
            System.out.println("Sending content to Azure Function for moderation" + content);
            ResponseEntity<ContentModerationResponse> response = restTemplate.postForEntity(
                    contentModerationUrl,
                    request,
                    ContentModerationResponse.class
            );

            // Check if the content is clean
            return response.getStatusCode().is2xxSuccessful() &&
                    "approved".equals(response.getBody().getStatus());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to moderate content", e);
        }
    }

    public Page<IndividualPost> getFriendsPosts(Long userId, Pageable pageable) {
        List<Long> friendsIds = userFriendsService.getFriendsIds(userId);

        if (friendsIds == null || friendsIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return postRepository.findPostsByUserIdInWithLimitedContent(friendsIds, pageable);
    }

    //delete posts by user id that were listened from rabbitmq delete user
    @RabbitListener(queues = RabbitMQConfig.USER_DELETE_QUEUE)
    public void deleteUserPosts(Long userId) {
        postRepository.deleteAllByUserId(userId);
    }

    @RabbitListener(queues = RabbitMQConfig.USER_UPDATE_QUEUE)
    public void updateUserPosts(UserUpdatedEvent userUpdatedEvent) {
        postRepository.updatePostUsername(userUpdatedEvent.getId(), userUpdatedEvent.getUsername());
    }
}
