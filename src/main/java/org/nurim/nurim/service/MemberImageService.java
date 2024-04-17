package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.dto.post.upload.UploadFileRequest;
import org.nurim.nurim.domain.dto.post.upload.UploadFileResponse;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberImageRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberImageService {

    private final MemberImageRepository memberImageRepository;
    private final MemberRepository memberRepository;
    private final FileUploadService fileUploadService;

    // 프로필 이미지 업로드
    @Transactional
    public void saveImage(Long memberId, String memberProfileImage, String profileName) {
        // 해당 memberId에 대한 MemberImage가 이미 존재하는지 확인
        Optional<MemberImage> existingImage = memberImageRepository.findByMember_MemberId(memberId);

        if (existingImage.isPresent()) {
            // 이미지가 존재하면 업데이트
            MemberImage memberImage = existingImage.get();
            memberImage.setMemberProfileImage(memberProfileImage);
            memberImage.setProfileName(profileName);

            // memberId를 사용하여 해당하는 Member 엔티티를 가져와서 설정
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found with id : " + memberId));
            memberImage.setMember(member);

            memberImageRepository.save(memberImage);
        } else {
            // 이미지가 존재하지 않으면 예외 throw
            throw new RuntimeException("Member image not found for memberId: " + memberId);
        }

    }

    // 프로필 이미지 삭제
    @Transactional
    public boolean deleteAndSetDefaultImage(Long memberId) {
        Optional<MemberImage> memberImageOptional = memberImageRepository.findByMember_MemberId(memberId);
        if (memberImageOptional.isPresent()) {
            MemberImage memberImage = memberImageOptional.get();

            // S3에서 파일 삭제
            boolean isRemovedFromS3 = fileUploadService.deleteFile(memberImage.getProfileName());

            // S3에서 이미지 삭제에 성공하면 DB에서 기본 이미지로 변경
            if (isRemovedFromS3) {
                // 기본 이미지로 변경할 이미지 경로
                String defaultImageUrl = "https://nurimplus.s3.ap-northeast-2.amazonaws.com/images/c4e11d02-3ed4-4475-9a57-18918721d381.jpeg";
                String defaultKey = "images/c4e11d02-3ed4-4475-9a57-18918721d381.jpeg";

                // DB에서 해당 회원의 이미지를 기본 이미지로 변경
                memberImage.setMemberProfileImage(defaultImageUrl);
                memberImage.setProfileName(defaultKey);
                memberImageRepository.save(memberImage);

                return true;
            } else {
                // S3에서의 이미지 삭제 실패 시 처리할 로직
                return false;
            }
        } else {
            // 해당 회원의 이미지가 존재하지 않을 경우 처리할 로직
            return false;
        }
    }

    // DB는 default 이미지 경로로 변경, S3에서는 삭제


//    public boolean setDefaultImage (Long memberId) {
//        // 기본 이미지 uuid
//        String defaultImage = "8590a967-d772-4872-9a98-4c7e3ad434f9";
//
//        try {
//            memberImageRepository.updateByMemberId(defaultImage, memberId);
//
//
//            return true;
//        } catch (Exception e) {
//            log.error("기본 이미지로 반환 실패" + e.getMessage());
//            return false;
//        }
//    }

}
