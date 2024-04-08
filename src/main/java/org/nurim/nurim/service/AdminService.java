package org.nurim.nurim.service;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.repository.MemberRepository;
import org.nurim.nurim.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 페이징 처리
    public Page<Member> getAllMembers(int page) {
        // 기본 페이지 크기
        int pageSize = 20;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        return memberRepository.findAll(pageRequest);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public Member updateMember(Long id, Member updatedMember) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
            // Update member properties
            member.setMemberEmail(updatedMember.getMemberEmail());
            member.setMemberPw(updatedMember.getMemberPw());
            member.setMemberNickname(updatedMember.getMemberNickname());
            // 추가적인 업데이트 로직 구현 가능
            return memberRepository.save(member);
        }
        return null;
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    // 회원 전환
    public Member updateMemberGrade(Long id, String grade) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
            member.setMemberGrade(grade);
            return memberRepository.save(member);
        }
        return null;
    }
}
