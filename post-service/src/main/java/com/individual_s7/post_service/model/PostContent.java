package com.individual_s7.post_service.model;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostContent {

    private String content;
    private Date createdAt;
}
