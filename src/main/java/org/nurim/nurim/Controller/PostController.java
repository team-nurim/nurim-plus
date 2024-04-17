package org.nurim.nurim.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.post.*;
import org.nurim.nurim.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Log4j2
public class PostController {

    private final PostService postService;

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/post/register/{adminId}")
    public ResponseEntity<CreatePostResponse> postCreate(
            @PathVariable Long adminId,
            @RequestBody CreatePostRequest request) {

        CreatePostResponse response = postService.createPost(adminId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/post/read{postId}")
    public ResponseEntity<ReadPostResponse> postRead(@PathVariable Long postId) {

        ReadPostResponse response = postService.readPostById(postId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/post/update{postId}")
    public ResponseEntity<UpdatePostResponse> postUpdate(@PathVariable Long postId,
                                                         @RequestBody UpdatePostRequest request){

        UpdatePostResponse response = postService.updatePost(postId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<DeletePostResponse> postDelete(@PathVariable Long postId) {

        DeletePostResponse response = postService.deletePost(postId);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/post/list")
    public ResponseEntity<Page<ReadPostResponse>> postReadAll(@PageableDefault(
            size = 15, sort = "postId", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReadPostResponse>  response = postService.readAllPost(pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/post/{category}")
    public Page<ReadPostResponse> readPostByKeywordAndCategory(
            @PathVariable String category,
            @PageableDefault(
                    size = 15, sort = "postId", direction = Sort.Direction.DESC) Pageable pageable) {
        // 키워드와 카테고리로 게시물 검색
        return postService.readPostsByKeywordAndCategory(category, pageable);
    }
}
