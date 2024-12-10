package com.individual_s7.post_service.controller;

import com.individual_s7.post_service.dto.IndividualPostResponse;
import com.individual_s7.post_service.dto.PostRequest;
import com.individual_s7.post_service.model.IndividualPost;
import com.individual_s7.post_service.assemblers.IndividualPostModelAssembler;
import org.springframework.hateoas.PagedModel;
import com.individual_s7.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final PagedResourcesAssembler<IndividualPost> pagedResourcesAssembler;


    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody PostRequest postRequest) {
        if(!postService.createOrUpdateUserPosts(postRequest)) {
            return ResponseEntity.ok("Post contains inappropriate content!");
        }
        return ResponseEntity.ok("Post created!");
//        postService.createOrUpdateUserPosts(postRequest);
//        return ResponseEntity.ok("Post created!");
    }

    @GetMapping("/getPosts")
    public PagedModel<IndividualPostResponse> getPostsResponse(
            @RequestHeader("X-user-Id") Long userId,
            Pageable pageable) {
        Page<IndividualPost> postPage = postService.getFriendsPosts(userId, pageable);
        IndividualPostModelAssembler assembler = new IndividualPostModelAssembler();
        return pagedResourcesAssembler.toModel(postPage, assembler);
    }
}

