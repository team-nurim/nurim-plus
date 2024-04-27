package org.nurim.nurim.Controller;

import org.nurim.nurim.service.MciIntegratedPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // 모든 오리진 허용
@RestController
@RequestMapping("/api/v1/mciintegratedpolicy")
public class MciIntegratedPolicyController<MciIntegratedPolicy> {

    @Autowired
    private MciIntegratedPolicyService service;

    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<MciIntegratedPolicy> getIntegratedPolicyById(@PathVariable Long id) {
        return null;
    }

    // 카테고리 'integrated'에 해당하는 통합 정책 정보를 가져오는 엔드포인트
    @GetMapping("/integratedfilter")
    public ResponseEntity<?> getFilteredPolicies(
            @RequestParam(required = false, defaultValue = "all") String region,
            @RequestParam(required = false, defaultValue = "all") String offerType,
            @RequestParam(required = false, defaultValue = "all") String businessEntity) {

        ResponseEntity<?> response = service.findByIntegratedFilters(region, offerType, businessEntity);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        } else {
            return response;
        }
    }
}