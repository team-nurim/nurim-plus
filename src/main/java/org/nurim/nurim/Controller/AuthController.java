package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "auth", description = "회원 인증/인가 API")
@RestController
@RequestMapping("/api/v1/auth")
@Log4j2
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/sample")
    public List<String> doA() {
        return Arrays.asList("AAA", "BBB", "CCC");
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
//        return authService.login(request);
//    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 리프레시 토큰을 검증하여 토큰이 유효하면 -> 토큰 무효화
        authService.logout();
        return ResponseEntity.ok("성공적으로 로그아웃 하였습니다.");
    }

}
