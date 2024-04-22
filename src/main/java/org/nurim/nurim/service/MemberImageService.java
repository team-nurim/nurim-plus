package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberImageRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

            // 이미지의 url과 key가 default와 일치하는지 확인
            String imageUrl = memberImage.getMemberProfileImage();
            String imageKey = memberImage.getProfileName();

            // 기본 이미지로 변경할 이미지 경로
            String defaultImageUrl = "https://nurimplus.s3.ap-northeast-2.amazonaws.com/images/b706c0f7-625a-485f-9d6e-2358822208bb.jpeg";
            String defaultKey = "images/b706c0f7-625a-485f-9d6e-2358822208bb.jpeg";

            if (imageUrl.equals(defaultImageUrl) && imageKey.equals(defaultKey)) {
                // 이미지의 url과 key가 default와 일치하면 삭제 로직을 적용하지 않음
                return true;
            }

            // S3에서 파일 삭제
            boolean isRemovedFromS3 = fileUploadService.deleteFile(memberImage.getProfileName());

            // S3에서 이미지 삭제에 성공하면 DB에서 기본 이미지로 변경
            if (isRemovedFromS3) {

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
}
