package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.community.ReadAllCommunityResponse;
import org.nurim.nurim.domain.dto.home.ReadHomeCommunityResponse;
import org.nurim.nurim.domain.dto.home.ReadHomePostResponse;
import org.nurim.nurim.service.CommunityService;
import org.nurim.nurim.service.HomeService;
import org.nurim.nurim.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Log4j2
public class HomeController {

    private final HomeService homeService;
    private final CommunityService communityService;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/read/{postId}")
    @Operation(summary = "정책 게시물 단건 조회")
    public ResponseEntity<ReadHomePostResponse> postRead(@PathVariable Long postId) {

        ReadHomePostResponse response = homeService.readHomePostById(postId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/postList")
    @Operation(summary = "정책 게시물 전체 조회")
    public ResponseEntity<Page<ReadHomePostResponse>> postReadAll(@PageableDefault(
            size = 4, sort = "postId", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReadHomePostResponse>  response = homeService.readAllHomePost(pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/communityList")
    @Operation(summary = "커뮤니티 게시물 전체 조회")
    public ResponseEntity<Page<ReadHomeCommunityResponse>> communityReadAll(@PageableDefault(
            size = 3, sort = "communityId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReadHomeCommunityResponse> communityResponses = communityService.getHomeCommunityList(pageable);
        return ResponseEntity.ok().body(communityResponses);
    }

}
