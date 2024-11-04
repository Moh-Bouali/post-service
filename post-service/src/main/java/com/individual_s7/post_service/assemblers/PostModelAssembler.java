package com.individual_s7.post_service.assemblers;

import com.individual_s7.post_service.dto.PostResponse;
import com.individual_s7.post_service.model.Post;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class PostModelAssembler implements RepresentationModelAssembler<Post, PostResponse> {

    @Override
    public PostResponse toModel(Post post) {
        return new PostResponse(post.getId(),post.getUserId(), post.getUsername(), post.getContent());
    }
}
