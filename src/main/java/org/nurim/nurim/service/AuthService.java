package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.LoginRequest;
import org.nurim.nurim.domain.dto.LoginResponse;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.repository.MemberImageRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;
    private final TokenProvider tokenProvider;

//    public LoginResponse login(LoginRequest request) {
//
//        String memberEmail = request.getMemberEmail();
//        String memberPw = request.getMemberPw();
//        String encryptedPw = passwordEncoder.encode(memberPw);
//
//        Member foundMember = memberService.readMemberByMemberEmail(memberEmail);
//
//        if (foundMember == null) {
//            throw new UsernameNotFoundException("이메일이 존재하지 않습니다.");
//        }
//        if (!foundMember.getMemberPw().equals(encryptedPw)) {
//            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
//        }
//
//        LoginResponse response =
//    }

    public void logout() {
        // 인증정보를 가져와 로그아웃 처리
        SecurityContextHolder.clearContext();
    }
}
