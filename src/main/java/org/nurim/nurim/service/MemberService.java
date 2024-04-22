package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.member.*;
import org.nurim.nurim.domain.entity.Expert;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.domain.entity.MemberRole;
import org.nurim.nurim.repository.MemberImageRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 일반 회원 가입
    @Transactional
    public CreateMemberResponse createMember(CreateMemberRequest request) {

        if (memberRepository.findMemberByMemberEmail(request.getMemberEmail()).isPresent()) {
            throw new DataIntegrityViolationException("이미 존재하는 회원입니다.");   // 전역예외처리 필요
        }

        // 초기 필드값 default 설정
        Member member = Member.builder()
                .memberEmail(request.getMemberEmail())
                .memberPw(passwordEncoder.encode(request.getMemberPw()))
                .memberNickname(request.getMemberNickname())
                .memberAge(30)
                .gender(true)
                .memberResidence("거주지 주소를 입력해주세요.")
                .memberMarriage(true)
                .memberIncome("소득 정보를 입력해주세요.")
                .type(false)
                .memberRole(MemberRole.USER)
                .build();

        Member savedMember = memberRepository.save(member);

        // 초기 프로필 이미지 URL 설정 (S3 버킷에 저장된 기본 이미지 URL)
        String defaultProfileImageUrl = "https://nurimplus.s3.ap-northeast-2.amazonaws.com/images/b706c0f7-625a-485f-9d6e-2358822208bb.jpeg";
        String defaultKey = "images/b706c0f7-625a-485f-9d6e-2358822208bb.jpeg";

        String defaultExpert = "증빙서류가 등록되지 않았습니다.";

        // 기본 이미지 경로 MemberImage에 설정하여 저장
        MemberImage memberImage = new MemberImage();
        memberImage.setMember(savedMember);
        memberImage.setMemberProfileImage(defaultProfileImageUrl);
        memberImage.setProfileName(defaultKey);
        memberImageRepository.save(memberImage);

        Expert expert = new Expert();
        expert.setMember(savedMember);
        expert.setExpertFile(defaultExpert);
        expert.setExpertFileName(defaultExpert);

        // 회원 정보에 이미지 정보 연결
        savedMember.setMemberImage(memberImage);
        savedMember.setExpert(expert);
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
                savedMember.isType(),
                savedMember.getMemberRole(),
                savedMember.getMemberImage().getMemberProfileImage(),
                savedMember.getExpert().getExpertFile()
        );

    }

    // 관리자 회원 가입
    @Transactional
    public CreateMemberResponse createAdmin(CreateMemberRequest request) {

        if (memberRepository.findMemberByMemberEmail(request.getMemberEmail()).isPresent()) {
            throw new DataIntegrityViolationException("이미 존재하는 회원입니다.");   // 전역예외처리 필요
        }

        // 초기 필드값 default 설정
        Member member = Member.builder()
                .memberEmail(request.getMemberEmail())
                .memberPw(passwordEncoder.encode(request.getMemberPw()))
                .memberNickname(request.getMemberNickname())
                .memberAge(30)
                .gender(true)
                .memberResidence("서울시 강남구 강남대로 405")
                .memberMarriage(true)
                .memberIncome("해당 없음")
                .type(false)
                .memberRole(MemberRole.ADMIN)
                .build();

        Member savedMember = memberRepository.save(member);

        // 초기 프로필 이미지 URL 설정 (S3 버킷에 저장된 기본 이미지 URL)
        String defaultProfileImageUrl = "https://nurimplus.s3.ap-northeast-2.amazonaws.com/images/b706c0f7-625a-485f-9d6e-2358822208bb.jpeg";
        String defaultKey = "images/b706c0f7-625a-485f-9d6e-2358822208bb.jpeg";

        String defaultExpert = "증빙서류가 등록되지 않았습니다.";

        // 기본 이미지 경로 MemberImage에 설정하여 저장
        MemberImage memberImage = new MemberImage();
        memberImage.setMember(savedMember);
        memberImage.setMemberProfileImage(defaultProfileImageUrl);
        memberImage.setProfileName(defaultKey);
        memberImageRepository.save(memberImage);

        Expert expert = new Expert();
        expert.setMember(savedMember);
        expert.setExpertFile(defaultExpert);
        expert.setExpertFileName(defaultExpert);

        // 회원 정보에 이미지 정보 연결
        savedMember.setMemberImage(memberImage);
        savedMember.setExpert(expert);
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
                savedMember.isType(),
                savedMember.getMemberRole(),
                savedMember.getMemberImage().getMemberProfileImage(),
                savedMember.getExpert().getExpertFile()
        );

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
            profileimageUrl = "https://nurimplus.s3.ap-northeast-2.amazonaws.com/images/b706c0f7-625a-485f-9d6e-2358822208bb.jpeg";
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

    // 특정 회원 개인 정보 수정
    @Transactional
    public UpdateMemberResponse updateMember(Long memberId, UpdateMemberRequest request) {

        // id 확인
        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        // Member 정보 업데이트
        foundMember.update(
                passwordEncoder.encode(request.getMemberPw()),
                request.getMemberNickname(),
                request.getMemberAge(),
                request.isGender(),
                request.getMemberResidence(),
                request.isMemberMarriage(),
                request.getMemberIncome(),
                request.isType());

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
    // 특정 회원 내 맞춤 정보 수정
    @Transactional
    public UpdateMemberResponse updateMemberInfo(Long memberId, UpdateMemberInfoRequest request) {

        // id 확인
        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        // Member 정보 업데이트
        foundMember.update(
                foundMember.getMemberPw(),
                foundMember.getMemberNickname(),
                request.getMemberAge(),
                request.isGender(),
                request.getMemberResidence(),
                request.isMemberMarriage(),
                request.getMemberIncome(),
                request.isType());

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
    public DeleteMemberResponse deleteMember(Long memberId) {

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        memberRepository.delete(foundMember);

        return new DeleteMemberResponse(foundMember.getMemberId());

    }


    // context에서 회원정보 가져오기
    public Member getMember(HttpServletRequest request) {

        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("🍎 authentication name : " + authentication.getName());

        if(authentication == null || !authentication.isAuthenticated()) {
            log.info("인증 객체를 찾을 수 없습니다.");
        }

        String username = authentication.getName();

        Member accessMember = memberRepository.findMemberByMemberEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 memberId로 조회된 회원 정보가 없습니다."));

        return accessMember;

    }

    public Member readMemberByMemberEmail(String username) {
        Member foundMember = memberRepository.findMemberByMemberEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("😥해당 이메일로 회원을 찾을 수 없습니다."));

        return foundMember;
    }

}