package com.individual_s7.post_service.assemblers;

import com.individual_s7.post_service.controller.PostController;
import com.individual_s7.post_service.dto.IndividualPostResponse;
import com.individual_s7.post_service.model.IndividualPost;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class IndividualPostModelAssembler extends RepresentationModelAssemblerSupport<IndividualPost, IndividualPostResponse> {

    public IndividualPostModelAssembler() {
        super(PostController.class, IndividualPostResponse.class);
    }

    @Override
    public IndividualPostResponse toModel(IndividualPost entity) {
        IndividualPostResponse response = new IndividualPostResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setUsername(entity.getUsername());
        response.setContent(entity.getContent());
        response.setCreatedAt(entity.getCreatedAt());
        // Add links or other HATEOAS elements if necessary
        return response;
    }
}

