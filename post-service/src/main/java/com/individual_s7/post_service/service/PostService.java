package com.individual_s7.post_service.service;

import com.individual_s7.post_service.configuration.RabbitMQConfig;
import com.individual_s7.post_service.dto.PostRequest;
import com.individual_s7.post_service.event.UserUpdatedEvent;
import com.individual_s7.post_service.model.IndividualPost;
import com.individual_s7.post_service.model.Post;
import com.individual_s7.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserFriendsService userFriendsService;

    public void createOrUpdateUserPosts(PostRequest postRequest) {
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
