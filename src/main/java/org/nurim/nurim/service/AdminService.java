package org.nurim.nurim.service;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.member.*;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    // 페이징 처리
    public Page<Member> getAllMembersPaged(int page) {
        // 기본 페이지 크기
        int pageSize = 20;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return memberRepository.findAll(pageRequest);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // 회원 상세 정보 조회
    public ReadMemberResponse readMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            // Member 엔티티를 ReadMemberResponse DTO로 변환
            return new ReadMemberResponse(
                    member.getMemberId(),
                    member.getMemberEmail(),
                    member.getMemberPw(),
                    member.getMemberNickname(),
                    member.getMemberAge(),
                    member.isGender(),
                    member.getMemberResidence(),
                    member.isMemberMarriage(),
                    member.getMemberIncome(),
                    member.isType(),
                    member.getMemberImage().getMemberProfileImage(),
                    member.getExpert().getExpertFile()
            );
        }
        return null;
    }

    // 회원 정보 수정
    @Transactional
    public UpdateMemberResponse updateMemberInfo(Long memberId, UpdateMemberInfoRequest request) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            // 업데이트 요청 받은 정보로 회원 엔티티 업데이트
//            member.setMemberPw(member.getMemberPw());
//            member.setMemberNickname(member.getMemberNickname());
//            member.setMemberAge(request.getMemberAge());
//            member.setGender(request.isGender());
//            member.setMemberResidence(request.getMemberResidence());
//            member.setMemberMarriage(request.isMemberMarriage());
//            member.setMemberIncome(request.getMemberIncome());
//            member.setType(request.isType());
//            member.setMemberImage();
//            member.setExpertFile(request.getExpertFile());

            member.update(
                    member.getMemberPw(),
                    member.getMemberNickname(),
                    request.getMemberAge(),
                    request.isGender(),
                    request.getMemberResidence(),
                    request.isMemberMarriage(),
                    request.getMemberIncome(),
                    request.isType());

            memberRepository.save(member);

            // 업데이트된 회원 정보 반환
            return new UpdateMemberResponse(
                    member.getMemberId(),
                    member.getMemberEmail(),
                    member.getMemberPw(),
                    member.getMemberNickname(),
                    member.getMemberAge(),
                    member.isGender(),
                    member.getMemberResidence(),
                    member.isMemberMarriage(),
                    member.getMemberIncome(),
                    member.isType(),
                    member.getMemberImage().getMemberProfileImage(),
                    member.getExpert().getExpertFile()
            );
        }
        return null;
    }

    // 회원 삭제
    @Transactional
    public DeleteMemberResponse deleteMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            memberRepository.delete(member);
            return new DeleteMemberResponse(memberId);
        }
        return null;
    }



}
