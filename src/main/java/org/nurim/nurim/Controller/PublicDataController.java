package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.nurim.nurim.service.MemberService;
import org.nurim.nurim.service.PublicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 클라이언트의 요청을 받아 서비스를 호출하고 결과를 반환, Http 요청에 대한 처리
@Tag(name = "PublicData", description = "공공 데이터 API")
@RestController
@RequestMapping("/api")
public class PublicDataController {

    private final PublicDataService publicDataService;

    @Autowired
    public PublicDataController(PublicDataService publicDataService) {
        this.publicDataService = publicDataService;
    }

    @GetMapping("/data")
    public ResponseEntity<String> getData() {
        String data = publicDataService.getDataFromApi();
        return ResponseEntity.ok(data);
    }
}