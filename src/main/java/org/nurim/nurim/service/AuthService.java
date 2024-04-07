package org.nurim.nurim.service;

import org.nurim.nurim.config.auth.CustomAuthenticationManager;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.LoginRequest;
import org.nurim.nurim.domain.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

    public LoginResponse authenticateMember(LoginRequest request) {

        try {
            Authentication authentication = customAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getMemberEmail(), request.getMemberPw()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();   // null발생
            String memberEmail = userDetails.getUsername();
            String token = tokenProvider.generateToken(memberEmail);

            // 토큰이 null인 경우에 대한 처리
            if (token != null) {
                return new LoginResponse(token);
            } else {
                // 토큰이 null인 경우에 대한 예외 처리 또는 로그
                throw new IllegalStateException("토큰 생성에 실패했습니다.");
            }
        } catch (AuthenticationException e) {
            // 사용자 인증이 실패한 경우에 대한 예외 처리 또는 로그
            throw new IllegalStateException("사용자 인증에 실패했습니다.", e);
        }

//        return new LoginResponse(token);


    }
}
