package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.entity.Post;
import org.nurim.nurim.domain.entity.PostImage;
import org.nurim.nurim.repository.PostImageRepository;
import org.nurim.nurim.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@Log4j2
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository; // PostRepository 추가
    private final String uploadPath;

    @Autowired
    public PostImageService(PostImageRepository postImageRepository, PostRepository postRepository, @Value("${org.yeolmae.upload.path}") String uploadPath) {
        this.postImageRepository = postImageRepository;
        this.postRepository = postRepository;
        this.uploadPath = uploadPath;
    }


    @Transactional
    public void saveImage(Long postId, String imagePath, String thumbPath) {
        PostImage postImage = new PostImage();
        postImage.setImage_detail(imagePath);
        postImage.setImage_thumb(thumbPath);

        // postId를 사용하여 해당하는 Post 엔티티를 가져와서 설정
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        postImage.setPost(post);


        postImageRepository.save(postImage);
    }

    @Transactional
    public Map<String, Boolean> deleteImage(Long postImageId) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isRemovedFromDatabase = false;

        // postImageId가 null이 아닌 경우에만 삭제 진행
        if (postImageId != null) {
            try {
                // 데이터베이스에서 이미지 정보 삭제
                postImageRepository.deleteById(postImageId);
                isRemovedFromDatabase = true; // 삭제 성공 시 true로 설정
            } catch (Exception e) {
                // 삭제 실패 시 에러 로그 출력
                log.error("Failed to delete image from the database: " + e.getMessage());
            }
        } else {
            log.warn("postImageId is null. Cannot delete image from the database.");
        }

        // 결과를 response 맵에 추가
        response.put("result", isRemovedFromDatabase);
        return response;
    }
}

