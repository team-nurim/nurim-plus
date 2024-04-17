package org.nurim.nurim.config.auth;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    // 로그인 성공 시 동작 정의 : 인증에 성공했을 경우 호출되어 토큰 생성
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // HTTP 응답 콘텐츠 타입을 JSON으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.info(authentication);
        log.info(authentication.getName());   // username 추출

        Map<String, Object> claim = Map.of("memberEmail", authentication.getName());
        // access token 유효기간 1일
        String accessToken = tokenProvider.generateToken(claim, 1);
//        // refresh token 유효기간 30일
//        String refreshToken = tokenProvider.generateToken(claim, 30);

        Gson gson = new Gson();

        // access, refresh token 포함하는 map 생성
        Map<String, String> keyMap = Map.of("accessToken", accessToken);
        String jsonStr = gson.toJson(keyMap);   // map 객체를 JSON 문자열로 변환

        response.getWriter().println(jsonStr);   // JSON 문자열을 HTTP 응답에 기록하여 클라이언트에 반환

        return;
    }
}
