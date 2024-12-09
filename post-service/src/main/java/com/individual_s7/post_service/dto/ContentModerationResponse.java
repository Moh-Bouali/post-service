package com.individual_s7.post_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentModerationResponse {
    private String content;
    private String status; // "approved" or "flagged"
}
