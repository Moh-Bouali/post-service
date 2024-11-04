package com.individual_s7.post_service.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

@Getter
@Setter
public class IndividualPostResponse extends RepresentationModel<IndividualPostResponse> {
    private String id;
    private Long userId;
    private String username;
    private String content;
    private Date createdAt;
    // Getters and Setters
}
