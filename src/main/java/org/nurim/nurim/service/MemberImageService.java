package org.nurim.nurim.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.AmazonS3ResourceStorage;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberImageService {

    private final MemberImageRepository memberImageRepository;
    private final AmazonS3ResourceStorage amazonS3ResourceStorage;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://i.stack.imgur.com/l60Hf.png";

    @Value("${org.yeolmae.upload.path}")
    private String uploadPath;

    @Transactional
    public void saveImage(String imagePath, Long memberId) {
        // 해당 memberId에 대한 MemberImage가 이미 존재하는지 확인
        Optional<MemberImage> existingImage = memberImageRepository.findByMember_MemberId(memberId);
        if (existingImage.isPresent()) {
            // 이미지가 존재하면 업데이트
            MemberImage memberImage = existingImage.get();
            memberImage.setMemberProfileImage(imagePath);
            memberImageRepository.save(memberImage);
        } else {
            // 이미지가 존재하지 않으면 예외 throw
            throw new RuntimeException("Member image not found for memberId: " + memberId);
        }
    }
//    @Transactional
//    public void saveImage(String imagePath, Member member) {
//
//        MemberImage memberImage = new MemberImage();
//        memberImage.setMemberProfileImage(imagePath);
//        memberImage.setMember(member);
//
//        memberImageRepository.save(memberImage);
//    }

    // 프로필 이미지 조회
    public String getProfileImageFileName(Long memberId) {

        Optional<MemberImage> memberImageOptional = memberImageRepository.findByMember_MemberId(memberId);

        return memberImageOptional.map(MemberImage::getMemberProfileImage).orElse(DEFAULT_PROFILE_IMAGE_URL);
    }

    // 프로필 이미지 삭제
    @Transactional
    public Map<String, Boolean> deleteImage(Long memberId) {

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemoved = false;

        try {
            // memberId로 프로필 이미지 조회
            MemberImage memberImage = memberImageRepository.findByMember_MemberId(memberId)
                    .orElse(null);

            if (memberImage != null) {
                String fileName = memberImage.getMemberProfileImage();

                // S3에서 이미지 삭제
                amazonS3.deleteObject(bucket, fileName);

                // DB에서 이미지 삭제
                memberImageRepository.delete(memberImage);
                isRemoved = true;

                // 초기 프로필 이미지 URL 설정 (S3 버킷에 저장된 기본 이미지 URL)
                String defaultProfileImageUrl = "https://nurimplus1.s3.ap-northeast-2.amazonaws.com/default-image.jpg";

                // Default 이미지로 변경
                memberImage.setMemberProfileImage(defaultProfileImageUrl);
                memberImageRepository.save(memberImage);
            } else {
                // 해당 memberId에 대한 프로필 이미지가 없는 경우
                log.warn("No profile image found for memberId: " + memberId);
            }
        } catch (AmazonServiceException e) {
            // S3에서 삭제 실패한 경우
            log.error("Failed to delete profile image from S3: " + e.getMessage());
        } catch (Exception e) {
            // 그 외 에러 발생 시
            log.error("Failed to delete profile image and update to default: " + e.getMessage());
        }

        response.put("result", isRemoved);
        return response;

    }


//    // 프로필 이미지 삭제
//    @Transactional
//    public Map<String, Boolean> deleteImage(Long memberId) {
//
//        Map<String, Boolean> response = new HashMap<>();
//        boolean isRemoved = false;
//
//        try {
//            // memberId를 기반으로 회원의 프로필 이미지를 찾음
//            MemberImage memberImage = memberImageRepository.findByMember_MemberId(memberId)
//                    .orElse(null);
//
//            if (memberImage != null) {
//                String fileName = memberImage.getMemberProfileImage();
//
//                // 프로필 이미지 파일 삭제
//                File file = new File(uploadPath + File.separator + fileName);
//                if (file.exists()) {
//                    isRemoved = file.delete();
//                }
//
//                // 파일 삭제가 성공한 경우 DB에서도 삭제
//                if (isRemoved) {
//                    memberImageRepository.delete(memberImage);
////                    memberImageRepository.updateMemberImageByFileName(fileName, DEFAULT_PROFILE_IMAGE_URL); // default로 변경
//                }
//            } else {
//                // 해당 memberId에 대한 프로필 이미지가 없는 경우
//                log.warn("No profile image found for memberId: " + memberId);
//            }
//        } catch (Exception e) {
//            // 에러 발생 시 로그 출력
//            log.error("Failed to delete profile image: " + e.getMessage());
//        }
//
//        response.put("result", isRemoved);
//        return response;
//
//    }


}
