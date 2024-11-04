package com.individual_s7.post_service.repository;

import com.individual_s7.post_service.model.UserFriends;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserFriendsRepository extends MongoRepository<UserFriends, Long> {
}
