package com.individual_s7.post_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndividualPost {
    private String id; // You can assign the original document id or generate a new one
    private Long userId;
    private String username;
    private String content;
    private Date createdAt;
}

