package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {


    // email 정보가 일치하는 회원 객체 반환
    Optional<Member> findMemberByMemberEmail(String memberEmail);


    Optional<Member> findByMemberId(Long memberId);

    // email 정보가 일치하는 회원 객체 반환
    Optional<Member> findMemberByMemberEmail(String memberEmail);
}
