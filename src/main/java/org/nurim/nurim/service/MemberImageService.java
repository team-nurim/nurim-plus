package org.nurim.nurim.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberImageRepository;
import org.springframework.beans.factory.annotation.Value;
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
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 프로필 이미지 업로드
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

    // 프로필 이미지 조회
    public String getProfileImageFileName(Long memberId) {

        Optional<MemberImage> memberImageOptional = memberImageRepository.findByMember_MemberId(memberId);

        return memberImageOptional.map(MemberImage::getMemberProfileImage).orElse("https://nurimplus.s3.ap-northeast-2.amazonaws.com/default-image.jpg");
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

}
