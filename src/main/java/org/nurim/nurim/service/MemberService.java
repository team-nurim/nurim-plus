package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.member.*;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberImageRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private static final String DEFAULT_PROFILE_IMAGE_URL = "/images/default-image.jpg";

    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    // 회원 정보 입력
    @Transactional
    public CreateMemberResponse createMemberInfo(CreateMemberRequest request) {

        // 회원 정보 유효성 검증 (클래스 별도 생성 예정)
        // validateMemberRequest(request);

        if (memberRepository.findMemberByMemberEmail(request.getMemberEmail()).isPresent()) {
            throw new DataIntegrityViolationException("이미 존재하는 회원입니다.");   // 전역예외처리 필요
        }

        Member member = Member.builder()
                .memberEmail(request.getMemberEmail())
                .memberPw(passwordEncoder.encode(request.getMemberPw()))
                .memberNickname(request.getMemberNickname())
                .memberAge(request.getMemberAge())
                .gender(request.isGender())
                .memberResidence(request.getMemberResidence())
                .memberMarriage(request.isMemberMarriage())
                .memberIncome(request.getMemberIncome())
                .type(request.isType())
                .build();

        Member savedMember = memberRepository.save(member);

        // 기본 이미지 경로 MemberImage에 설정하여 저장
        MemberImage memberImage = new MemberImage();
        memberImage.setMember(savedMember);
        memberImage.setMemberProfileImage(DEFAULT_PROFILE_IMAGE_URL); // 정적 경로 참조
        memberImageRepository.save(memberImage);

        // 회원 정보에 이미지 정보 연결
        savedMember.setMemberImage(memberImage);
        memberRepository.save(savedMember);

        return new CreateMemberResponse(savedMember.getMemberId(),
                savedMember.getMemberEmail(),
                savedMember.getMemberPw(),
                savedMember.getMemberNickname(),
                savedMember.getMemberAge(),
                savedMember.isGender(),
                savedMember.getMemberResidence(),
                savedMember.isMemberMarriage(),
                savedMember.getMemberIncome(),
                savedMember.isType());

    }

    // 특정 회원 조회
    public ReadMemberResponse readMemberById(Long memberId) {

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        String profileimageUrl;
        if(foundMember.getMemberImage() != null && foundMember.getMemberImage().getMemberProfileImage() != null) {
            // 프로필 이미지가 등록되어 있는 경우
            profileimageUrl = foundMember.getMemberImage().getMemberProfileImage();
        } else {
            // 프로필 이미지가 등록되지 않은 경우
            profileimageUrl = "기본 프로필 이미지 URL";
        }

        String expertFileUrl;
        if(foundMember.getExpert() != null && foundMember.getExpert().getExpertFile() != null) {
            // 증빙 서류가 등륵되어 있는 경우
            expertFileUrl = foundMember.getExpert().getExpertFile();
        } else {
            // 증빙 서류가 등록되지 않은 경우
            expertFileUrl = "증빙서류가 등록되지 않았습니다.";
        }

        return new ReadMemberResponse(
                foundMember.getMemberId(),
                foundMember.getMemberEmail(),
                foundMember.getMemberPw(),
                foundMember.getMemberNickname(),
                foundMember.getMemberAge(),
                foundMember.isGender(),
                foundMember.getMemberResidence(),
                foundMember.isMemberMarriage(),
                foundMember.getMemberIncome(),
                foundMember.isType(),
                profileimageUrl,
                expertFileUrl);

    }

    // 특정 회원 정보 수정
    @Transactional
    public UpdateMemberResponse updateMemberInfo(Long memberId, UpdateMemberRequest request) {

        // id 확인
        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        String rawPw = request.getMemberPw();
        String encPw = passwordEncoder.encode(rawPw);

        // Member 정보 업데이트
        foundMember.update(
                encPw,
                request.getMemberNickname(),
                request.getMemberAge(),
                request.isGender(),
                request.getMemberResidence(),
                request.isMemberMarriage(),
                request.getMemberIncome(),
                request.isType());
//
//        // MemberImage 정보 업데이트
//        String newMemberProfileImage = request.getMemberProfileImage(); // 새로운 이미지 정보
//        UpdateMemberImageRequest imageRequest = new UpdateMemberImageRequest(newMemberProfileImage); // 이미지 정보 갖는 객체
//        memberImageService.updateMemberImage(foundMember.getMemberImage().getProfileImageId(), imageRequest);

        // Expert 자격증 이미지 정보 업데이트


        return new UpdateMemberResponse(foundMember.getMemberId(),
                foundMember.getMemberEmail(),
                foundMember.getMemberPw(),
                foundMember.getMemberNickname(),
                foundMember.getMemberAge(),
                foundMember.isGender(),
                foundMember.getMemberResidence(),
                foundMember.isMemberMarriage(),
                foundMember.getMemberIncome(),
                foundMember.isType(),
                foundMember.getMemberImage().getMemberProfileImage(),
                foundMember.getExpert().getExpertFile());

    }

    // 회원 탈퇴
    @Transactional
    public DeleteMemberResponse deleteMemberInfo(Long memberId) {

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        memberRepository.delete(foundMember);

        return new DeleteMemberResponse(foundMember.getMemberId());

    }

    // context에서 회원정보 가져오기
    public Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();   // 사용자 이메일 추출

        Member member = memberRepository.findMemberByMemberEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

        return member;
    }

    public Member findMemberByMemberEmail(String username) {
        Member foundMember = memberRepository.findMemberByMemberEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일로 회원을 찾을 수 없습니다."));

        return foundMember;
    }

    public Member getMemberById(Long memberId) {
        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 memberId로 회원을 찾을 수 없습니다."));

        return foundMember;
    }

//    @Transactional
//    public PatchMemberResponse updateMemberPart(Long memberId, PatchMemberRequest request) {
//
//        Member foundMember = memberRepository.findById(memberId)
//                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));
//
//
//
//        return new PatchMemberResponse(foundMember.getMemberId(), foundMember.getMemberEmail(), foundMember.getMemberPw(), foundMember.getMemberNickname(),
//                foundMember.getMemberAge(), foundMember.isGender(), foundMember.getMemberResidence(), foundMember.isMemberMarriage(), foundMember.getMemberIncome(), foundMember.isType());
//
//    }

}
