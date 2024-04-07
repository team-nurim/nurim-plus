package org.nurim.nurim.config.auth;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.service.PrincipalDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    @Autowired
    private final PrincipalDetailsService principalDetailsService;

    @Autowired
    private final PasswordEncoder passwordEncoder;


    public AuthenticationManager authenticationManager(HttpSecurity http)  throws Exception {
        // AuthenticationManger 설정
        // http로부터 인증설정 객체 가져오기
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(principalDetailsService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 사용자가 제공한 인증 정보
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 사용자 확인
        UserDetails userDetails = principalDetailsService.loadUserByUsername(username);

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // 인증 성공시 반환할 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
}
