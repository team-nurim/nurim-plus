package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
//import org.nurim.nurim.config.auth.CustomAuthenticationManager;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.LoginRequest;
import org.nurim.nurim.domain.dto.TokenDTO;
//import org.nurim.nurim.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "auth", description = "회원 인증/인가 API")
@RestController
@ResponseBody
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Log4j2
@CrossOrigin(origins = "*", methods = RequestMethod.POST)
public class AuthController {

//    @Autowired
//    private CustomAuthenticationManager customAuthenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

//    @Autowired
//    private AuthService authService;

    @GetMapping("/sample")
    public List<String> doA() {
        return Arrays.asList("AAA", "BBB", "CCC");
    }

//    @PostMapping("/login")
//    public ResponseEntity<TokenDTO> authenticateMember(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
//
//        // AuthService를 통해 로그인 처리하고 토큰을 받아옴
//        TokenDTO tokenDTO = authService.authenticateMember(request);
//
//        if(tokenDTO.getAccessToken() != null && tokenProvider.validateToken(tokenDTO.getAccessToken())) {
//
//            response.addHeader(tokenProvider.AUTHORIZATION_HEADER, tokenDTO.getAccessToken());
//            return new ResponseEntity<>(tokenDTO, HttpStatus.OK);
//
//        } else {
//
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpServletRequest request) {
//        // 헤더로부터 access token 추출
//        String accessToken = tokenProvider.getAccessToken(request);
//
//        // 토큰 무효화
//        boolean isLoggedout = tokenProvider.invalidateToken(accessToken);
//
//
//        //
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

}
