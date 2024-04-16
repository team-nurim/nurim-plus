package org.nurim.nurim.Controller;

import com.google.gson.Gson;
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
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
//
//
//        return ResponseEntity.ok(new LoginResponse(jsonStr));
//    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 리프레시 토큰을 검증하여 토큰이 유효하면 -> 토큰 무효화
        authService.logout();
        return ResponseEntity.ok("성공적으로 로그아웃 하였습니다.");
    }

}
