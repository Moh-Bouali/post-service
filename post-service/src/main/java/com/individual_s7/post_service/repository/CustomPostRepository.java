package com.individual_s7.post_service.repository;

import com.individual_s7.post_service.model.IndividualPost;
import com.individual_s7.post_service.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPostRepository {

    void updatePostUsername(Long userId, String newUsername);

    //Page<Post> findPostsByUserIdInWithLimitedContent(List<Long> friendsIds, Pageable pageable, int contentLimit);

    Page<IndividualPost> findPostsByUserIdInWithLimitedContent(List<Long> friendsIds, Pageable pageable);
}
