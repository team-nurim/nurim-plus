package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.LoginRequest;
import org.nurim.nurim.domain.dto.LoginResponse;
import org.nurim.nurim.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "auth", description = "회원 인증/인가 API")
@RestController
@RequestMapping("/api/v1/auth")
@Log4j2
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenProvider tokenProvider;


    @GetMapping("/sample")
    public List<String> doA() {
        return Arrays.asList("AAA", "BBB", "CCC");
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
//        return authService.login(request);
//    }


//    @PostMapping("/generateToken")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//
//        // 사용자 인증 (실제 구현은 데이터베이스 검색 등이 필요합니다)
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getMemberEmail(),
//                        loginRequest.getMemberPw()
//                )
//        );
//
//        // 인증 성공시 SecurityContext에 인증 객체 저장
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // 토큰 생성에 필요한 정보 저장
//        Map<String, Object> valueMap = new HashMap<>();
//        valueMap.put("princial", authentication.getPrincipal());
//        valueMap.put("authorities", authentication.getAuthorities());
//
//        // JWT 토큰 생성
//        String accessToken = tokenProvider.generateToken(valueMap, 1);
//        String refreshToken = tokenProvider.generateToken(valueMap, 30);
//
//        // 응답 header에 토큰값 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + accessToken);
//        headers.set("Refresh", "Bearer " + refreshToken);
//
//        return new ResponseEntity<>(new LoginResponse(accessToken, refreshToken), headers, HttpStatus.OK);
//    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 리프레시 토큰을 검증하여 토큰이 유효하면 -> 토큰 무효화
        authService.logout();
        return ResponseEntity.ok("성공적으로 로그아웃 하였습니다.");
    }

}
