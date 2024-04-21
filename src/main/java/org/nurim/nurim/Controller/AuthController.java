package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.service.AuthService;
import org.nurim.nurim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Auth", description = "회원 인증/인가 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Log4j2
public class AuthController {

    @Autowired
    private AuthService authService;
    private final MemberService memberService;


    @GetMapping("/sample")
    public List<String> doA() {
        return Arrays.asList("AAA", "BBB", "CCC");
    }


//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
//        // 로그인 정보로 토큰 생성
//        LoginResponse response = authService.login(request);
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 리프레시 토큰을 검증하여 토큰이 유효하면 -> 토큰 무효화
        authService.logout();
        return ResponseEntity.ok("성공적으로 로그아웃 하였습니다.");
    }

}
