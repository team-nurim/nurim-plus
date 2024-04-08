package org.nurim.nurim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberImageService {

    private final MemberImageRepository memberImageRepository;

    @Value("${org.yeolmae.upload.path}")
    private String uploadPath;


    // 프로필 이미지 등록
    @Transactional
    public void saveImage(String imagePath) {

        MemberImage memberImage = new MemberImage();
        memberImage.setMemberProfileImage(imagePath);

        memberImageRepository.save(memberImage);
    }

    @Transactional
    public Map<String, Boolean> deleteImage(String fileName) {

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemoved = false;

        // 파일 이름이 비어 있지 않은 경우에만 삭제 진행
        if (!StringUtils.isEmpty(fileName)) {
            File file = new File(uploadPath + File.separator + fileName);

            try {
                // 프로필 이미지 파일 삭제
                if (file.exists()) {
                    isRemoved = file.delete();
                }

                // 파일 삭제가 성공한 경우에만 데이터베이스에서 이미지 정보 삭제
                if (isRemoved) {
                    memberImageRepository.deleteByFileName(fileName);
                }
            } catch (Exception e) {
                // 에러 발생 시 로그 출력
                log.error("Failed to delete image: " + e.getMessage());
            }
        }

        // 결과를 response 맵에 추가
        response.put("result", isRemoved);
        return response;

    }

}
