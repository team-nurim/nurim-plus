package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.community.*;
import org.nurim.nurim.service.CommunityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;


    @PostMapping("/communityCreate")
    @Operation(summary = "게시물 작성")
    public ResponseEntity<CreateCommunityResponse> createCommunity(@RequestBody CreateCommunityRequest request){
        Long memberId = request.getMemberId();
        CreateCommunityResponse response = communityService.communityCreate(memberId, request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/communityRead/{communityId}")
    @Operation(summary = "게시물 단위조회 및 조회수")
    public ResponseEntity<ReadCommunityResponse> readCommunity(@PathVariable Long communityId){
        ReadCommunityResponse response = communityService.communityRead(communityId);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("/communityDelete/{communityId}")
    @Operation(summary = "게시물 삭제")
    public ResponseEntity<DeleteCommunityResponse> deleteCommunity(@PathVariable Long communityId){
        DeleteCommunityResponse response = communityService.communityDelete(communityId);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("/communityUpdate/{communityId}")
    @Operation(summary = "게시물 수정")
    public ResponseEntity<UpdateCommunityResponse> updateCommunity(@PathVariable Long communityId, @RequestBody UpdateCommunityRequest request){
        UpdateCommunityResponse response = communityService.communityUpdate(communityId, request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/list/{category}")
    @Operation(summary = "게시물 카테고리 별 조회 페이징")
    public ResponseEntity<Page<ReadCommunityResponse>> readCommunityList(@PathVariable String category, @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, 20, Sort.by("communityId").descending());
        Page<ReadCommunityResponse> communityResponsePage = communityService.getCommunityListByCategory(category, pageable);
        return ResponseEntity.ok().body(communityResponsePage);
    }
}
