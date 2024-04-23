package org.nurim.nurim.Controller;

import org.nurim.nurim.service.MciHousingPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/integratedpolicy")
public class MciIntegratedPolicyController<MciIntegratedPolicy> {

    @Autowired
    private MciHousingPolicyService service;

    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<MciIntegratedPolicy> getIntegratedPolicyById(@PathVariable Long id) {
        return null;
    }

    // 카테고리 'integrated'에 해당하는 통합 정책 정보를 가져오는 엔드포인트
    @GetMapping("/integratedfilter")
    public ResponseEntity<List<MciIntegratedPolicy>> getFilteredPolicies(
            @RequestParam(required = false, defaultValue = "all") String region,
            @RequestParam(required = false, defaultValue = "all") String businessClassification,
            @RequestParam(required = false, defaultValue = "all") String businessEntity) {

        List<MciIntegratedPolicy> policies = service.findByCategoryFilters("integrated", region, businessClassification, businessEntity);
        if (policies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(policies);
    }
}

