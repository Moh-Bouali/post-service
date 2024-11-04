package com.individual_s7.post_service.dto;

import com.individual_s7.post_service.model.PostContent;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse extends RepresentationModel<PostResponse> {
    private String id;
    private Long userId;
    private String username;
    private List<PostContent> content;
}
