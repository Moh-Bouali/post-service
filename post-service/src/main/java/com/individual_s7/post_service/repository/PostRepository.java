package com.individual_s7.post_service.repository;

import com.individual_s7.post_service.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, Long>, CustomPostRepository {
    //Page<Post> findByUserIdIn(List<Long> friendsIds, Pageable pageable);

    Optional<Post> findByUserId(Long userId);

    void deleteAllByUserId(Long userId);

}
