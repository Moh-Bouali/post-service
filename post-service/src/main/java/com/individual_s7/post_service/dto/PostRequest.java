package com.individual_s7.post_service.dto;

import com.individual_s7.post_service.model.PostContent;
import java.util.List;


public record PostRequest(Long userId, String username, List<PostContent> content){
}
