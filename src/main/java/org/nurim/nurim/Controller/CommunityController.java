package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.community.*;
import org.nurim.nurim.domain.dto.reply.ReadReplyResponse;
import org.nurim.nurim.service.CommunityService;
import org.nurim.nurim.service.ReplyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

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
    @Operation(summary = "게시물 단위조회 및 조회수", description = "게시물을 조회하고 조회한만큼 조회수가 오릅니다.")
    public ResponseEntity<ReadCommunityResponse> readCommunity(@PathVariable Long communityId){
        ReadCommunityResponse response = communityService.communityRead(communityId);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("/communityDelete/{communityId}")
    @Operation(summary = "게시물 삭제", description = "게시물 memberId에 속한 유저만 삭제가 가능합니다.")
    public ResponseEntity<DeleteCommunityResponse> deleteCommunity(@PathVariable Long communityId, String memberEmail) {
        try {
            DeleteCommunityResponse response = communityService.communityDelete(communityId, memberEmail);
            return ResponseEntity.ok().body(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/communityUpdate/{communityId}")
    @Operation(summary = "게시물 수정")
    public ResponseEntity<UpdateCommunityResponse> updateCommunity(@PathVariable Long communityId, String memberEmail, @RequestBody UpdateCommunityRequest request) throws AccessDeniedException {
        UpdateCommunityResponse response = communityService.communityUpdate(communityId, memberEmail, request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/categoryPage/{category}")
    @Operation(summary = "게시물 카테고리 별 게시물조회 페이징")
    public ResponseEntity<Page<ReadSearchResponse>> readCommunityList(@PathVariable String category, @RequestParam(defaultValue = "0")int page){
        Pageable pageable = PageRequest.of(0, 20, Sort.by("communityId").descending());
        Page<ReadSearchResponse> communityResponsePage = communityService.getCommunityListByCategory(category, pageable);
        return ResponseEntity.ok().body(communityResponsePage);
    }

    /**
     *인기 많은 게시글
     */
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/popular")
    @Operation(summary = "게시글 조회수 순으로 5개 나열")
    public ResponseEntity<List<ReadCountsCommunitiesResponse>> getPopularCommunities(){
        PageRequest pageRequest = PageRequest.of(0, 5 ,Sort.by("ViewCounts").descending());
        Page<ReadCountsCommunitiesResponse> popularCommunities = communityService.findPopularCommunities(pageRequest);
        List<ReadCountsCommunitiesResponse> popularCommunitiesList = popularCommunities.getContent();
        return ResponseEntity.ok(popularCommunitiesList);
    }

    /**
     * 키워드에 따른 게시글 검색
     */
    @PostMapping("/community/Search")
    @Operation(summary = "검색기능" , description = "제목,카테고리,작성자 기준으로 각 게시물을 검색을 할수있습니다.")
    public ResponseEntity<Page<ReadSearchResponse>> searchCommunity(@RequestParam (required = false)String title ,
                                                                   @RequestParam (required = false) String communityCategory,
                                                                   @RequestParam (required = false) String memberNickname){
        PageRequest pageRequest = PageRequest.of(0,20,Sort.by("title").descending());
        Page<ReadSearchResponse> searchResultPage;
        if(title != null && communityCategory !=null && memberNickname !=null){
            searchResultPage = communityService.SearchTitleAndCategoryAndMemberNickName(title,communityCategory,memberNickname,pageRequest);
        } else if (title !=null) {
            searchResultPage = communityService.SearchTitle(title, pageRequest);
        } else if (communityCategory != null){
            searchResultPage = communityService.SearchCategory(communityCategory, pageRequest);
        } else if ( memberNickname!= null){
        searchResultPage = communityService.SearchMemberNickName(memberNickname, pageRequest);
        } else{
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(searchResultPage);
    }

    /**
     * 댓글 기다리는 문의글
     */
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/community/Inquire")
    @Operation(summary = "문의글" , description = "댓글이 없는 게시물중 조회수가 높은 10건을 노출")
    public ResponseEntity<List<ReadInquireResponse>> inquireCommunity(){
        PageRequest pageRequest = PageRequest.of(0,10,Sort.by("viewCounts").descending());
        Page<ReadInquireResponse> inquireResponses = communityService.NoRepliesButHit(pageRequest);
        List<ReadInquireResponse> inquireResponseList = inquireResponses.getContent();
        return ResponseEntity.ok(inquireResponseList);
    }
}
