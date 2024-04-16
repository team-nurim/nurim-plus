package org.nurim.nurim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.repository.MemberImageRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    @Autowired
    private MemberService memberService;

    @Autowired
    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;


//    public ResponseEntity<?> login(LoginRequest request) {
//        try {
//            /** 사용자 인증 객체 생성 및 TokenDTO 반환 */
//
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

    public void logout() {
        // 인증정보를 가져와 로그아웃 처리
        SecurityContextHolder.clearContext();
    }
}
