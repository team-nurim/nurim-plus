package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.member.*;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public CreateMemberResponse createMemberInfo(CreateMemberRequest request) {

        Member member = Member.builder()
                .memberEmail(request.getMemberEmail())
                .memberPw(request.getMemberPw())
                .memberNickname(request.getMemberNickname())
                .memberAge(request.getMemberAge())
                .gender(request.isGender())
                .memberResidence(request.getMemberResidence())
                .memberMarriage(request.isMemberMarriage())
                .memberIncome(request.getMemberIncome())
                .type(request.isType())
                .build();

        Member savedMember = memberRepository.save(member);

        return new CreateMemberResponse(savedMember.getMemberId(), savedMember.getMemberEmail(), savedMember.getMemberPw(), savedMember.getMemberNickname(),
                savedMember.getMemberAge(), savedMember.isGender(), savedMember.getMemberResidence(), savedMember.isMemberMarriage(), savedMember.getMemberIncome(), savedMember.isType());

    }

    public ReadMemberResponse readMemberById(Long memberId) {

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        return new ReadMemberResponse(foundMember.getMemberId(), foundMember.getMemberEmail(), foundMember.getMemberPw(), foundMember.getMemberNickname(),
                foundMember.getMemberAge(), foundMember.isGender(), foundMember.getMemberResidence(), foundMember.isMemberMarriage(), foundMember.getMemberIncome(), foundMember.isType());

    }

    @Transactional
    public UpdateMemberResponse updateMemberInfo(Long memberId, UpdateMemberRequest request) {

        // id 확인
        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        // Dirty Checking : DB에서 변경된 사항이 감지되면 자동으로 변경해줌.
        // update => MemberRepository에서 DB의 변경된 사항이 감지되면 자동으로 변경
        foundMember.update(request.getMemberPw(), request.getMemberNickname(), request.getMemberAge(), request.isGender(),
                request.getMemberResidence(), request.isMemberMarriage(), request.getMemberIncome(), request.isType());

        return new UpdateMemberResponse(foundMember.getMemberId(), foundMember.getMemberEmail(), foundMember.getMemberPw(), foundMember.getMemberNickname(),
                foundMember.getMemberAge(), foundMember.isGender(), foundMember.getMemberResidence(), foundMember.isMemberMarriage(), foundMember.getMemberIncome(), foundMember.isType());

    }


    @Transactional
    public DeleteMemberResponse deleteMemberInfo(Long memberId) {

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        memberRepository.delete(foundMember);

        return new DeleteMemberResponse(foundMember.getMemberId());

    }

    @Transactional
    public PatchMemberResponse updateMemberPart(Long memberId, PatchMemberRequest request) {

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));



        return new PatchMemberResponse(foundMember.getMemberId(), foundMember.getMemberEmail(), foundMember.getMemberPw(), foundMember.getMemberNickname(),
                foundMember.getMemberAge(), foundMember.isGender(), foundMember.getMemberResidence(), foundMember.isMemberMarriage(), foundMember.getMemberIncome(), foundMember.isType());

    }
}
