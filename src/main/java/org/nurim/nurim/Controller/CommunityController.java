package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.community.*;
import org.nurim.nurim.service.CommunityService;
import org.nurim.nurim.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "Community", description = "커뮤니티 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final MemberService memberService;

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/communityCreate/{accessToken}")
    @Operation(summary = "게시물 작성")
    public ResponseEntity<CreateCommunityResponse> createCommunity(@PathVariable String accessToken, @RequestBody CreateCommunityRequest request){
        CreateCommunityResponse response = communityService.communityCreate(accessToken, request);
        return ResponseEntity.ok().body(response);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/communityRead/{communityId}")
    @Operation(summary = "게시물 단위조회 및 조회수", description = "게시물을 조회하고 조회한만큼 조회수가 오릅니다.")
    public ResponseEntity<ReadCommunityResponse> readCommunity(@PathVariable Long communityId){
        ReadCommunityResponse response = communityService.communityRead(communityId);
        return ResponseEntity.ok().body(response);
    }
    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/communityDelete/{communityId}/{accessToken}")
    @Operation(summary = "게시물 삭제", description = "게시물 memberId에 속한 유저만 삭제가 가능합니다.")
    public ResponseEntity<DeleteCommunityResponse> deleteCommunity(@PathVariable Long communityId,@PathVariable String accessToken) {
        try {
            DeleteCommunityResponse response = communityService.communityDelete(communityId, accessToken);
            return ResponseEntity.ok().body(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/communityUpdate/{communityId}/{accessToken}")
    @Operation(summary = "게시물 수정")
    public ResponseEntity<UpdateCommunityResponse> updateCommunity(@PathVariable Long communityId, @PathVariable String accessToken, @RequestBody UpdateCommunityRequest request) throws AccessDeniedException {
        UpdateCommunityResponse response = communityService.communityUpdate(communityId, accessToken, request);
        return ResponseEntity.ok().body(response);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/communityList")
    @Operation(summary = "게시물 전체 조회")
    public ResponseEntity<Page<ReadAllCommunityResponse>> readAllCommunity(@PageableDefault(
            size = 20, sort = "communityId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReadAllCommunityResponse> communityResponses = communityService.getCommunityList(pageable);
        return ResponseEntity.ok().body(communityResponses);
    }


    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/categoryPage/{category}")
    @Operation(summary = "게시물 카테고리 별 게시물조회 페이징")
    public ResponseEntity<Page<ReadSearchResponse>> readCommunityList(
            @PathVariable String category,
            @PageableDefault( size = 20, sort = "communityId",direction = Sort.Direction.DESC) Pageable pageable){
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
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/community/Search")
    @Operation(summary = "검색기능" , description = "제목,카테고리,작성자 기준으로 각 게시물을 검색을 할수있습니다.")
    public ResponseEntity<Page<ReadSearchResponse>> searchCommunity(@RequestParam (required = false)String keyword ,
                                                                   @RequestParam (required = false) String communityCategory,
                                                                   @RequestParam (required = false) String memberNickname,
            @PageableDefault(size = 20, sort = "communityId", direction = Sort.Direction.DESC) Pageable pageable){

        Page<ReadSearchResponse> searchResultPage;
        if(keyword != null && communityCategory !=null && memberNickname !=null){
            searchResultPage = communityService.SearchTitleAndCategoryAndMemberNickName(keyword,communityCategory,memberNickname,pageable);
        } else if (keyword !=null) {
            searchResultPage = communityService.SearchTitle(keyword, pageable);
        } else if (communityCategory != null){
            searchResultPage = communityService.SearchCategory(communityCategory, pageable);
        } else if ( memberNickname!= null){
        searchResultPage = communityService.SearchMemberNickName(memberNickname, pageable);
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
    /**
     * 좋아요 한 게시글 멤버 아이디 마다 보기
     */
//    @CrossOrigin(origins = "http://localhost:8081")
//    @GetMapping("/heart/{accessToken}")
//    @Operation(summary = "좋아요한 게시글")
//    public ResponseEntity<ReadCommunityResponse> heartCommunity(@PathVariable String accessToken){
//        ReadCommunityResponse response = communityService.readByHeart(accessToken);
//        return ResponseEntity.ok().body(response);
//    }
}
