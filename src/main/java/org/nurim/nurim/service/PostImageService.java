package org.nurim.nurim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.entity.PostImage;
import org.nurim.nurim.repository.PostImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@Log4j2
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final String uploadPath;


    @Transactional
    public void saveImage(String imagePath, String thumbPath) {
        PostImage postImage = new PostImage();
        postImage.setImage_detail(imagePath);
        postImage.setImage_thumb(thumbPath);
        postImageRepository.save(postImage);
    }
    @Autowired
    public PostImageService(PostImageRepository postImageRepository, @Value("${org.yeolmae.upload.path}") String uploadPath) {
        this.postImageRepository = postImageRepository;
        this.uploadPath = uploadPath;
    }

    @Transactional
    public Map<String, Boolean> deleteImage(String fileName) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isRemoved = false;

        // 파일 이름이 비어 있지 않은 경우에만 삭제 진행
        if (!StringUtils.isEmpty(fileName)) {
            File file = new File(uploadPath + File.separator + fileName);
            File thumbFile = new File(uploadPath + File.separator + "thumb_" + fileName);

            try {
                // 이미지 파일 삭제
                if (file.exists()) {
                    isRemoved = file.delete();
                }

                // 썸네일 파일 삭제
                if (thumbFile.exists()) {
                    thumbFile.delete();
                }

                // 데이터베이스에서 이미지 정보 삭제
                if (isRemoved) {
                    postImageRepository.deleteByFileName(fileName);
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




