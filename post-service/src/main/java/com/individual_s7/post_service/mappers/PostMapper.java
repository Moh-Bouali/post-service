package com.individual_s7.post_service.mappers;

import com.individual_s7.post_service.dto.PostResponse;
import com.individual_s7.post_service.model.Post;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {

    public static PostResponse toPostResponse(Post post) {
        if (post == null) {
            return null;
        }

        PostResponse postResponse = PostResponse.builder()
                .userId(post.getUserId())
                .content(post.getContent())
                .build();
        // Map other fields as necessary.

        return postResponse;
    }

    // map post to post response
    public static PostResponse mapPostToPostResponse(Post post){
        return toPostResponse(post);
    }

    // map list of posts to list of post responses
    public static List<PostResponse> mapPostsToPostResponses(List<Post> posts){
        return posts.stream().map(PostMapper::toPostResponse).collect(Collectors.toList());
    }

}
