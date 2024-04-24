package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.community.ReadAllCommunityResponse;
import org.nurim.nurim.domain.dto.community.ReadSearchResponse;
import org.nurim.nurim.service.RecommendService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Recommend", description = "커뮤니티 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/communityList")
    @Operation(summary = "게시물 전체 조회")
    public ResponseEntity<Page<ReadAllCommunityResponse>> readAllCommunity(@PageableDefault(
            size = 20, sort = "communityId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReadAllCommunityResponse> communityResponses = recommendService.getCommunityList(pageable);
        return ResponseEntity.ok().body(communityResponses);
    }


    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/community/Search")
    @Operation(summary = "검색기능" , description = "제목,카테고리,작성자 기준으로 각 게시물을 검색을 할수있습니다.")
    public ResponseEntity<Page<ReadSearchResponse>> searchCommunity(@RequestParam(required = false)String title ,
                                                                    @RequestParam (required = false) String communityCategory,
                                                                    @RequestParam (required = false) String memberNickname,
                                                                    @PageableDefault(size = 20, sort = "communityId", direction = Sort.Direction.DESC) Pageable pageable){

        Page<ReadSearchResponse> searchResultPage;
        if(title != null && communityCategory !=null && memberNickname !=null){
            searchResultPage = recommendService.SearchTitleAndCategoryAndMemberNickName(title,communityCategory,memberNickname,pageable);
        } else if (title !=null) {
            searchResultPage = recommendService.SearchTitle(title, pageable);
        } else if (communityCategory != null){
            searchResultPage = recommendService.SearchCategory(communityCategory, pageable);
        } else if ( memberNickname!= null){
            searchResultPage = recommendService.SearchMemberNickName(memberNickname, pageable);
        } else{
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(searchResultPage);
    }
}
