package org.nurim.nurim.service;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.entity.Expert;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.ExpertRepository;
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
public class ExpertService {

    private final ExpertRepository expertRepository;
    private final AmazonS3 amazonS3;

    @Value("${org.yeolmae.upload.path}")
    private String uploadPath;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 자격증 이미지 등록
    @Transactional
    public void saveExpertFile(String imagePath, Long memberId) {

        Optional<Expert> existingImage = expertRepository.findByMember_MemberId(memberId);
        if (existingImage.isPresent()) {
            // 이미지가 존재하면 업데이트
            Expert expert = existingImage.get();
            expert.setExpertFile(imagePath);
            expertRepository.save(expert);
        } else {
            // 이미지가 존재하지 않으면 예외 throw
            throw new RuntimeException("Member image not found for memberId: " + memberId);
        }
//
//        Expert expert = new Expert();
//        expert.setExpertFile(imagePath);
//        expert.setMember(getExpertImageFileName(memberId));
//
//        expertRepository.save(expert);

    }

    // 자격증 이미지 조회
    public String getExpertImageFileName(Long memberId) {

        Optional<Expert> expertOptional = expertRepository.findByMember_MemberId(memberId);

        return expertOptional.map(Expert::getExpertFile).orElse("자격증 이미지가 존재하지 않습니다.");
    }

    // 자격증 이미지 삭제
    @Transactional
    public Map<String, Boolean> deleteExpertFile(Long memberId){

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemoved = false;

        try {
            // memberId를 기반으로 자격증 이미지를 찾음
            Expert expert = expertRepository.findByMember_MemberId(memberId)
                    .orElse(null);

            if (expert != null) {
                String fileName = expert.getExpertFile();

                // 자격증 이미지 파일 삭제
                File file = new File(uploadPath + File.separator + fileName);
                if (file.exists()) {
                    isRemoved = file.delete();
                }

                // 파일 삭제가 성공한 경우 DB에서도 삭제
                if (isRemoved) {

                    expertRepository.deleteByFileName(fileName);
                }
            } else {
                // 해당 memberId에 대한 자격증 이미지가 없는 경우
                log.warn("No profile image found for memberId: " + memberId);
            }
        } catch (Exception e) {
            // 에러 발생 시 로그 출력
            log.error("Failed to delete profile image: " + e.getMessage());
        }

        response.put("result", isRemoved);
        return response;
    }

}
