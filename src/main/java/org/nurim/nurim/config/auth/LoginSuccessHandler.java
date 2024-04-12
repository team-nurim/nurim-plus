package org.nurim.nurim.config.auth;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Log4j2
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    // 토큰 생성
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.info(authentication);
        log.info(authentication.getName());   // username 추출

        Map<String, Object> claim = Map.of("memberEmail", authentication.getName());
        // access token 유효기간 1일
        String accessToken = tokenProvider.generateToken(claim, 1);
        // refresh token 유효기간 30일
        String refreshToken = tokenProvider.generateToken(claim, 30);

        Gson gson = new Gson();

        Map<String, String> keyMap = Map.of("accessToken", accessToken, "refreshToken", refreshToken);

        String jsonStr = gson.toJson(keyMap);

        response.getWriter().println(jsonStr);

        return;
    }
}
