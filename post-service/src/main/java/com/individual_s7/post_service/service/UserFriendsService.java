package com.individual_s7.post_service.service;

import com.individual_s7.post_service.configuration.RabbitMQConfig;
import com.individual_s7.post_service.event.FriendshipEvent;
import com.individual_s7.post_service.model.UserFriends;
import com.individual_s7.post_service.repository.UserFriendsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableRabbit
public class UserFriendsService {

    private final UserFriendsRepository userFriendsRepository;

    public List<Long> getFriendsIds(Long userId) {
        UserFriends userFriends = userFriendsRepository.findById((userId)).orElseThrow(() -> new RuntimeException("User not found"));
        return userFriends != null ? userFriends.getFriendsIds() : new ArrayList<>();
    }

    @RabbitListener(queues = RabbitMQConfig.POST_FRIENDSHIP_RESPONSE_QUEUE)
    public void handleFriendshipEvent(FriendshipEvent event) {

        Optional<UserFriends> userFriendsOptional1 = userFriendsRepository.findById(event.getRequester_id());
        Optional<UserFriends> userFriendsOptional2 = userFriendsRepository.findById(event.getRequested_id());

        if(userFriendsOptional1.isPresent()) {
            UserFriends updatedUserFriend = userFriendsOptional1.get();
            updatedUserFriend.getFriendsIds().add(event.getRequested_id());
            userFriendsRepository.save(updatedUserFriend);
        } else {
            UserFriends userFriends = UserFriends.builder()
                    .userId(event.getRequester_id())
                    .friendsIds(Collections.singletonList(event.getRequested_id()))
                    .build();
            userFriendsRepository.save(userFriends);
        }

        if(userFriendsOptional2.isPresent()) {
            UserFriends updatedUserFriend = userFriendsOptional2.get();
            updatedUserFriend.getFriendsIds().add(event.getRequester_id());
            userFriendsRepository.save(updatedUserFriend);
        } else {
            UserFriends userFriends = UserFriends.builder()
                    .userId(event.getRequested_id())
                    .friendsIds(Collections.singletonList(event.getRequester_id()))
                    .build();
            userFriendsRepository.save(userFriends);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.USER_DELETE_QUEUE)
    public void handleUserDeleteEvent(Long userId) {
        userFriendsRepository.deleteById(userId);
        // Delete user in other users Friends list by deleting wherever the ID is found in UserFriends FriendsIds list
        List<UserFriends> userFriendsList = userFriendsRepository.findAll();
        for(UserFriends userFriends : userFriendsList) {
            userFriends.getFriendsIds().remove(userId);
            userFriendsRepository.save(userFriends);
        }
    }
}
