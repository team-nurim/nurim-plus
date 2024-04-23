package org.nurim.nurim.Controller;

import org.nurim.nurim.domain.entity.MciHousingPolicy;
import org.nurim.nurim.service.MciHousingPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // 모든 오리진 허용
@Controller
@RequestMapping("/api/v1/mcihousingpolicy")
public class MciHousingPolicyController {

    @Autowired
    private MciHousingPolicyService service;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("mciHousingPolicy", new MciHousingPolicy());
        return "mcipolicy"; // Thymeleaf 템플릿 뷰의 이름을 반환
    }

    @PostMapping
    public String createHousingPolicy(MciHousingPolicy mcipolicy) {
        service.saveHousingPolicy(mcipolicy);
        return "redirect:/mcihousingpolicy/list";
    }

    //API 엔드포인트
    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<MciHousingPolicy> getHousingPolicyById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/housingfilter")
    public ResponseEntity<?> getFilteredPolicies(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "all") String region,
            @RequestParam(required = false, defaultValue = "all") String businessClassification,
            @RequestParam(required = false, defaultValue = "all") String businessEntity) {

        ResponseEntity<?> response = service.findByFilters(category, region, businessClassification, businessEntity);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        } else {
            return response;
        }
    }
}



