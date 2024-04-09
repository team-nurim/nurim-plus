package org.nurim.nurim.service;

import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.config.auth.CustomAuthenticationManager;
import org.nurim.nurim.config.auth.PrincipalDetails;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.LoginRequest;
import org.nurim.nurim.domain.dto.LoginResponse;
import org.nurim.nurim.domain.dto.TokenDTO;
import org.nurim.nurim.domain.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AuthService {

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberService memberService;


    public TokenDTO authenticateMember(LoginRequest request) {
        /** 사용자 인증 객체 생성 및 TokenDTO 반환 */

        try {
            // 사용자 인증 객체 생성
            Authentication authentication = customAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getMemberEmail(), request.getMemberPw()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String memberEmail = authentication.getName();
//            String password = authentication.getCredentials().toString();

            Member foundMember = memberService.findMemberByMemberEmail(memberEmail);
            if(foundMember == null) {
                throw new IllegalStateException("회원 정보를 찾을 수 없습니다.");
            }

            TokenDTO tokenDTO = tokenProvider.generateTokenDTO(new PrincipalDetails(foundMember));
            if(tokenDTO == null) {
                throw new IllegalStateException("토큰 생성에 실패했습니다.");
            }

            // tokenDTO 반환
            return tokenDTO;

        } catch (AuthenticationException e) {

            // 사용자 인증이 실패한 경우에 대한 예외 처리 또는 로그
            throw new IllegalStateException("사용자 인증에 실패했습니다.");

        }
//        return new LoginResponse(token);

    }

//    public String logout(TokenDTO tokenDTO) {
//        String accessToken = tokenProvider.invalidateToken(tokenDTO.getAccessToken());
//
//        if(is)
//    }
}
