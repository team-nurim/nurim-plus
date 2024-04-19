package org.nurim.nurim.Controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.policy.ResponseVo;
import org.nurim.nurim.service.CallApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Policy", description = "정책 지원금 API")
@RestController
@RequiredArgsConstructor
public class PolicyController {
    private final CallApiService callApiService;

    @GetMapping("saveData")
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

}
