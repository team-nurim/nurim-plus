package org.nurim.nurim.config.auth;

import org.nurim.nurim.domain.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PrincipalDetails implements UserDetails {

    // Member 정보
    private static Member member;

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    public static PrincipalDetails of(String subject, String memberType) {
        return new PrincipalDetails(member);
    }

    // 회원유형 반환
    public String getMemberTypeToString() {
        if (member.isType()) {
            return "전문가";
        } else {
            return "일반회원";
        }
    }

    // member의 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getMemberRole().toString()));

        return authorities;
    }


    @Override
    public String getPassword() {
        return member.getMemberPw();
    }

    @Override
    public String getUsername() {
        return member.getMemberEmail();
    }

    public String getNickname() {
        return member.getMemberNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
