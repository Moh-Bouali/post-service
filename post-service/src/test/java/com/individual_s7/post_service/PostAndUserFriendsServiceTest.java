package com.individual_s7.post_service;

import com.individual_s7.post_service.dto.PostRequest;
import com.individual_s7.post_service.event.FriendshipEvent;
import com.individual_s7.post_service.event.UserUpdatedEvent;
import com.individual_s7.post_service.model.IndividualPost;
import com.individual_s7.post_service.model.Post;
import com.individual_s7.post_service.model.PostContent;
import com.individual_s7.post_service.model.UserFriends;
import com.individual_s7.post_service.repository.PostRepository;
import com.individual_s7.post_service.repository.UserFriendsRepository;
import com.individual_s7.post_service.service.PostService;
import com.individual_s7.post_service.service.UserFriendsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class PostAndUserFriendsServiceTest {

	@Mock
	private PostRepository postRepository;

	@Mock
	private UserFriendsRepository userFriendsRepository;
	@InjectMocks
	private PostService postService;

	@InjectMocks
	private UserFriendsService userFriendsService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// PostService Tests
	@Test
	void testCreateOrUpdateUserPosts_NewUser() {
		PostContent postContent = PostContent.builder()
				.content("First post")
				.createdAt(new Date()) // Use any mock Date
				.build();

		PostRequest postRequest = new PostRequest(1L, "user1", List.of(postContent));
		when(postRepository.findByUserId(1L)).thenReturn(Optional.empty());

		postService.createOrUpdateUserPosts(postRequest);

		verify(postRepository, times(1)).save(any(Post.class));
	}

	@Test
	void testCreateOrUpdateUserPosts_ExistingUser() {
		// Set up an existing Post with empty content
		Post existingPost = Post.builder()
				.userId(1L)
				.username("user1")
				.content(new ArrayList<>()) // Existing content
				.build();

		// Set up PostContent for the new request
		PostContent newContent = PostContent.builder()
				.content("New post")
				.createdAt(new Date())
				.build();

		// Set up PostRequest with the new content
		PostRequest postRequest = new PostRequest(1L, "user1", List.of(newContent));
		when(postRepository.findByUserId(1L)).thenReturn(Optional.of(existingPost));

		// Call the service method
		postService.createOrUpdateUserPosts(postRequest);

		// Verify content was added and saved
		assertEquals(1, existingPost.getContent().size());
		assertEquals("New post", existingPost.getContent().get(0).getContent());
		verify(postRepository, times(1)).save(existingPost);
	}

//	@Test
//	void testGetFriendsPosts_WithFriends() {
//		// Define friend IDs and mock posts to return
//		List<Long> friendsIds = List.of(2L, 3L);
//		UserFriends mockUserFriends = UserFriends.builder()
//				.userId(1L)
//				.friendsIds(friendsIds)
//				.build();
//
//		// Mock the response for the repository call
//		when(userFriendsRepository.findById(1L)).thenReturn(Optional.of((mockUserFriends)));
////		when(postRepository.findPostsByUserIdInWithLimitedContent(friendsIds, PageRequest.of(0, 5)))
////				.thenReturn(new PageImpl<>(List.of()));
//		//when(userFriendsService.getFriendsIds(1L)).thenReturn(friendsIds);
//
//		IndividualPost friendsPost = IndividualPost.builder()
//				.id("postId")
//				.userId(2L)
//				.username("friendUser")
//				.content("Friend's post")
//				.createdAt(new Date())
//				.build();
//
//		// Mocking pagination result with IndividualPost
//		Page<IndividualPost> page = new PageImpl<>(List.of(friendsPost));
//		when(postRepository.findPostsByUserIdInWithLimitedContent(friendsIds, PageRequest.of(0, 5))).thenReturn(page);
//
//		// Call the service method
//		Page<IndividualPost> result = postService.getFriendsPosts(1L, PageRequest.of(0, 5));
//
//		// Verify results and repository interaction
//		assertEquals(1, result.getTotalElements());
//		assertEquals("Friend's post", result.getContent().get(0).getContent());
//	}


	@Test
	void testDeleteUserPosts() {
		postService.deleteUserPosts(1L);

		verify(postRepository, times(1)).deleteAllByUserId(1L);
	}

	@Test
	void testUpdateUserPosts() {
		UserUpdatedEvent event = new UserUpdatedEvent(1L, "newUsername");

		postService.updateUserPosts(event);

		verify(postRepository, times(1)).updatePostUsername(1L, "newUsername");
	}

	// UserFriendsService Tests
	@Test
	void testGetFriendsIds_UserExists() {
		UserFriends userFriends = new UserFriends(1L, List.of(2L, 3L));
		when(userFriendsRepository.findById(1L)).thenReturn(Optional.of(userFriends));

		List<Long> result = userFriendsService.getFriendsIds(1L);

		assertEquals(2, result.size());
		assertEquals(2L, result.get(0));
	}

	@Test
	void testHandleFriendshipEvent_AddsFriendship() {
		FriendshipEvent event = new FriendshipEvent(1L, 2L, "user1", "user2");

		UserFriends requesterFriends = new UserFriends(1L, new ArrayList<>());
		UserFriends requestedFriends = new UserFriends(2L, new ArrayList<>());
		when(userFriendsRepository.findById(1L)).thenReturn(Optional.of(requesterFriends));
		when(userFriendsRepository.findById(2L)).thenReturn(Optional.of(requestedFriends));

		userFriendsService.handleFriendshipEvent(event);

		assertEquals(1, requesterFriends.getFriendsIds().size());
		assertEquals(1, requestedFriends.getFriendsIds().size());
		verify(userFriendsRepository, times(2)).save(any(UserFriends.class));
	}

	@Test
	void testHandleUserDeleteEvent() {
		UserFriends user1 = new UserFriends(1L, new ArrayList<>(List.of(2L, 3L)));
		UserFriends user2 = new UserFriends(2L, new ArrayList<>(List.of(1L)));
		UserFriends user3 = new UserFriends(3L, new ArrayList<>(List.of(1L)));

		when(userFriendsRepository.findAll()).thenReturn(List.of(user1, user2, user3));

		userFriendsService.handleUserDeleteEvent(1L);

		verify(userFriendsRepository, times(1)).deleteById(1L);
		assertEquals(0, user2.getFriendsIds().size());
		assertEquals(0, user3.getFriendsIds().size());
		verify(userFriendsRepository, times(3)).save(any(UserFriends.class));
	}
}
