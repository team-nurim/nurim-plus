package org.nurim.nurim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;


    // 로그인 - email로 사용자 정보 조회
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String memberEmail) throws UsernameNotFoundException {

        Member memberEntity = memberRepository.findMemberByMemberEmail(memberEmail)
                .orElseThrow(() -> new UsernameNotFoundException("로그인 정보를 확인하세요."));

        UserDetails userDetails = User.builder()
                .username(memberEntity.getMemberEmail())
                .password(memberEntity.getMemberPw())
//                .authorities("ROLE_USER")
                .build();

        log.info("💎userDetails : {}", userDetails);

        return userDetails;
    }
}
