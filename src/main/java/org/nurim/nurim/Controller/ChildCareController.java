package org.nurim.nurim.Controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.policy.ResponseVo;
import org.nurim.nurim.domain.entity.api.ChildCare;
import org.nurim.nurim.repository.PolicyRepositroy;
import org.nurim.nurim.service.CallApiService;
import org.nurim.nurim.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Policy", description = "정책 지원금 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommend")
public class ChildCareController {

    private final PolicyRepositroy policyRepositroy;
    private final CallApiService callApiService;
    private final MemberService memberService;

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

    @GetMapping("/childCare")
    public ResponseEntity<List<ChildCare>> getAllChildCare() {
        List<ChildCare> list = policyRepositroy.findAll();
        return ResponseEntity.ok(list);
    }
}

// 백엔드 API 엔드포인트가 완성된 상태입니다. 이 컨트롤러에는 두 가지 주요 기능이 있습니다:
// 1. 'saveData' 엔드포인트: 외부 API 호출을 수행하고 그 결과를 ResponseVo 객체로 반환합니다. 이는 주로 데이터 수집 및 저장 과정을 처리하기 위한 엔드포인트입니다.
// 2. '/childCare' 엔드포인트: 데이터베이스에서 모든 보육 정책 데이터를 조회하고 이를 리스트 형태로 반환합니다.
//     이엔드포인트는 프론트엔드에서 해당 데이터를 조회할 수 있게 해 줍니다.
//이제 두 엔드포인트 모두 필요한 기능을 제공하므로 API가 올바르게 구성된 것으로 보입니다.
//이제 프론트엔드에서 이 API를 사용하여 데이터를 조회하거나 처리한 데이터를 확인할 수 있습니다.
//추가적으로 필요한 보안, 예외 처리, API 문서화 등을 고려하면 더욱 견고한 API 서비스를 제공할 수 있을 것입니다.