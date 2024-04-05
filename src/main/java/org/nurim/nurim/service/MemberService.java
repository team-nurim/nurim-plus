package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.*;
import org.nurim.nurim.domain.entity.Expert;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    @Autowired
    private final MemberRepository memberRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateMemberResponse createMember(CreateMemberRequest request) {
        if (memberRepository.findMemberByMemberEmail(request.getMemberEmail()).isPresent()) {
            throw new DataIntegrityViolationException("이미 존재하는 회원입니다.");   // 전역예외처리 필요
        }

        Member member = Member.builder()
                .memberEmail(request.getMemberEmail())
                .memberPw(passwordEncoder.encode(request.getMemberPw()))
                .memberNickname(request.getMemberNickname())
                .build();

        Member savedMember = memberRepository.save(member);

        return new CreateMemberResponse(
                savedMember.getMemberId(),
                savedMember.getMemberEmail(),
                savedMember.getMemberPw(),
                savedMember.getMemberNickname()
                );
    }

    public ReadMemberResponse readMemberById(Long memberId) {

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 id로 조회된 회원이 존재하지 않습니다."));

        String profileimageUrl;
        if(foundMember.getMemberImage() != null && foundMember.getMemberImage().getMemberProfileImage() != null) {
            // 프로필 이미지가 등록되어 있는 경우
            profileimageUrl = foundMember.getMemberImage().getMemberProfileImage();
        } else {
            profileimageUrl = "기본 프로필 이미지 URL";
        }

        String expertFile;
        if(foundMember.getExpert() != null && foundMember.getExpert().getExpertFile() != null) {
            // 증빙 서류가 등륵되어 있는 경우
            expertFile = foundMember.getExpert().getExpertFile();
        } else {
            expertFile = "";
        }

//        if(foundMember.getExpert() == null) {
//            Expert expert = Expert.builder()
//                    .expertFile("default file")
//                    .build();
//        }

        return new ReadMemberResponse(
                foundMember.getMemberId(),
                foundMember.getMemberEmail(),
                foundMember.getMemberPw(),
                foundMember.getMemberNickname(),
                foundMember.getMemberAge(),
                foundMember.isGender(),
                foundMember.getMemberResidence(),
                foundMember.isMarried(),
                foundMember.getMemberIncome(),
                foundMember.isType(),
                foundMember.getMemberImage().getMemberProfileImage(),
                foundMember.getExpert().getExpertFile()
        );
    }

    @Transactional
    public UpdateMemberResponse updateMember(Long memberId, UpdateMemberRequest request) {

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        foundMember.update(
                request.getMemberPw(),
                request.getMemberNickname(),
                request.getMemberAge(),
                request.isGender(),
                request.getMemberResidence(),
                request.isMarried(),
                request.getMemberIncome(),
                request.isType()
        );

        return new UpdateMemberResponse(
                foundMember.getMemberId(),
                foundMember.getMemberEmail(),
                foundMember.getMemberPw(),
                foundMember.getMemberNickname(),
                foundMember.getMemberAge(),
                foundMember.isGender(),
                foundMember.getMemberResidence(),
                foundMember.isMarried(),
                foundMember.getMemberIncome(),
                foundMember.isType()
        );
    }

    @Transactional
    public DeleteMemberResponse deleteMember(Long memberId) {

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

        memberRepository.delete(foundMember);

        return new DeleteMemberResponse(foundMember.getMemberId());
    }

//    public Member getMember() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//    }



}
