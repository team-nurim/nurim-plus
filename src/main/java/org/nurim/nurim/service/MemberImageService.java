package org.nurim.nurim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    private static final String DEFAULT_PROFILE_IMAGE_URL = "/images/default-image.jpg";

    @Value("${org.yeolmae.upload.path}")
    private String uploadPath;

    // 프로필 이미지 등록
//    @Transactional
//    public String saveImage(MultipartFile file, Long memberId) throws IOException {
//        String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
//        Path uploadDir = Paths.get(uploadPath);
//        Files.createDirectories(uploadDir);
//        Path filePath = uploadDir.resolve(fileName);
//        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//
//        // 데이터베이스에 이미지 경로 저장
//        MemberImage memberImage = new MemberImage();
//        memberImage.setMemberProfileImage(fileName);
//        memberImage.setMember(memberService.getMemberById(memberId));
//        memberImageRepository.save(memberImage);
//
//        return fileName;
//    }
    @Transactional
    public void saveImage(String imagePath, Member member) {

        MemberImage memberImage = new MemberImage();
        memberImage.setMemberProfileImage(imagePath);
        memberImage.setMember(member);

        memberImageRepository.save(memberImage);
    }

    // 프로필 이미지 조회
//    public Resource getProfileImageResource(Long memberId) {
//        Optional<MemberImage> memberImageOptional = memberImageRepository.findByMember_MemberId(memberId);
//        String fileName = memberImageOptional
//        Path imagePath = Paths.get(uploadPath).resolve(fileName);
//    }
    public String getProfileImageFileName(Long memberId) {

        Optional<MemberImage> memberImageOptional = memberImageRepository.findByMember_MemberId(memberId);

        return memberImageOptional.map(MemberImage::getMemberProfileImage).orElse(DEFAULT_PROFILE_IMAGE_URL);
//        // memberId로 이미지 조회
//        MemberImage memberImage = memberImageRepository.findByMember_MemberId(memberId)
//                .orElseThrow(() -> new EntityNotFoundException("프로필 이미지가 존재하지 않습니다."));

//        if (memberImage.getMemberProfileImage() != null) {
//            fileName = memberImage.getMemberProfileImage();
//        } else {
//            fileName = "기본프로필이미지.png";
//        }
//
//        return fileName;
    }

    // 프로필 이미지 삭제
    @Transactional
    public Map<String, Boolean> deleteImage(Long memberId) {

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemoved = false;

        try {
            // memberId를 기반으로 회원의 프로필 이미지를 찾음
            MemberImage memberImage = memberImageRepository.findByMember_MemberId(memberId)
                    .orElse(null);

            if (memberImage != null) {
                String fileName = memberImage.getMemberProfileImage();

                // 프로필 이미지 파일 삭제
                File file = new File(uploadPath + File.separator + fileName);
                if (file.exists()) {
                    isRemoved = file.delete();
                }

                // 파일 삭제가 성공한 경우 DB에서도 삭제
                if (isRemoved) {
                    // default 이미지로 변경하는 로직 추가
                    memberImageRepository.updateMemberImageByFileName(fileName, DEFAULT_PROFILE_IMAGE_URL); // default로 변경
                }
            } else {
                // 해당 memberId에 대한 프로필 이미지가 없는 경우
                log.warn("No profile image found for memberId: " + memberId);
            }
        } catch (Exception e) {
            // 에러 발생 시 로그 출력
            log.error("Failed to delete profile image: " + e.getMessage());
        }

        response.put("result", isRemoved);
        return response;

//        // 파일 이름이 비어 있지 않은 경우에만 삭제 진행
//        if (!StringUtils.isEmpty(fileName)) {
//            File file = new File(uploadPath + File.separator + fileName);
//
//            try {
//                // 프로필 이미지 파일 삭제
//                if (file.exists()) {
//                    isRemoved = file.delete();
//                }
//
//                // 파일 삭제가 성공한 경우 DB에서도 삭제
//                if (isRemoved) {
//                    // default 이미지로 변경하는 로직 추가
//                    memberImageRepository.updateMemberImageByFileName(fileName, DEFAULT_PROFILE_IMAGE_URL); // default로 변경
//                }
//            } catch (Exception e) {
//                // 에러 발생 시 로그 출력
//                log.error("Failed to delete image: " + e.getMessage());
//            }
//        }
//        // 결과를 response 맵에 추가
//        response.put("result", isRemoved);
//        return response;
    }


}
