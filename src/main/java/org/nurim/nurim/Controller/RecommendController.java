package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.policy.ReadChildCareSearchResponse;
import org.nurim.nurim.domain.dto.policy.ReadMciHousingSearchResponse;
import org.nurim.nurim.domain.dto.policy.ReadMciIntegratedSearchResponse;
import org.nurim.nurim.domain.dto.policy.ResponseVo;
import org.nurim.nurim.service.CallApiService;
import org.nurim.nurim.service.RecommendService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Recommend", description = "정책 추천 API")
@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final CallApiService callApiService;
    private final RecommendService policyService;


    @GetMapping("/childCare/saveData")
    @CrossOrigin(origins = "http://localhost:8081")
    @Operation(summary = "ChildCare DB 업데이트")
    public ResponseEntity<ResponseVo> testOpenAPI() {
        // 변수 설정 (요청할 api 파라미터에맞게)
        String apiKey = "7a21da61f8c34441b3cb174cc1623aa0";
        String type = "json";
        Integer plndex = 5;
        Integer pSize = 10;

        callApiService.callApi(apiKey, type, plndex, pSize);

        // ResponseVo 객체를 생성하여 응답설정으로 체크,확인가능
        ResponseVo responseVo = new ResponseVo();
        responseVo.setUcd("성공!");
        responseVo.setMessage("데이터 처리 완료");

        //클라이언트에 응답
        return ResponseEntity.ok(responseVo);
    }


    @ResponseBody
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/getFilteredChildcare")
    @Operation(summary = "childcare 목록 조회: 지역/키워드로 필터")
    public ResponseEntity<?> getFilteredChildcare(
            @RequestParam(required = true) String region,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReadChildCareSearchResponse> resultPage;

        if(region != null && keyword != null){
            // 지역과 키워드로 검색
            resultPage = policyService.searchChildCareByRegionAndKeyword(region, keyword, pageable);
        } else if (region != null) {
            // 지역으로만 검색
            resultPage = policyService.searchChildCareByRegion(region, pageable);
        } else{
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(resultPage);
    }


    @ResponseBody
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/getFilteredHousingPolicy")
    @Operation(summary = "MciHousingPolicy 목록 조회: 지역/키워드로 필터")
    public ResponseEntity<?> getFilteredMciHousingPolicy(
            @RequestParam(required = true) String region,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReadMciHousingSearchResponse> resultPage;

        if(region != null && keyword != null){
            // 지역과 키워드로 검색
            resultPage = policyService.searchMciHousingByRegionAndKeyword(region, keyword, pageable);
        } else if (region != null) {
            // 지역으로만 검색
            resultPage = policyService.searchMciHousingByRegion(region, pageable);
        } else{
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(resultPage);
    }

    @ResponseBody
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/getFilteredIntegratedPolicy")
    @Operation(summary = "MciIntegratedPolicy 목록 조회: 지역/키워드로 필터")
    public ResponseEntity<?> getFiltereMciIntegratedPolicy(
            @RequestParam(required = true) String region,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReadMciIntegratedSearchResponse> resultPage;

        if(region != null && keyword != null){
            // 지역과 키워드로 검색
            resultPage = policyService.searchMciIntegratedByRegionAndKeyword(region, keyword, pageable);
        } else if (region != null) {
            // 지역으로만 검색
            resultPage = policyService.searchMciIntegratedByRegion(region, pageable);
        } else{
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(resultPage);
    }

}
