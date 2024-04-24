package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.service.MciHousingPolicyService;
import org.nurim.nurim.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Recommend", description = "정책 추천 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecommendController {

//    @Autowired
//    private RecommendService recommendService;
//
//    @ResponseBody
//    @GetMapping("/")
//    public ResponseEntity<?> getAllPolicies(
//            @RequestParam(required = false) String category,
//            @RequestParam(required = false, defaultValue = "all") String region,
//            @RequestParam(required = false, defaultValue = "all") String businessClassification,
//            @RequestParam(required = false, defaultValue = "all") String businessEntity) {
//
//        ResponseEntity<?> response = recommendService.findByFilters(category, region, businessClassification, businessEntity);
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return ResponseEntity.ok(response.getBody());
//        } else {
//            return response;
//        }
//    }

}
