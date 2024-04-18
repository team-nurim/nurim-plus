package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.entity.Post;
import org.nurim.nurim.domain.entity.PostImage;
import org.nurim.nurim.repository.PostImageRepository;
import org.nurim.nurim.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository; // PostRepository 추가
    private final FileUploadService fileUploadService;

    @Transactional(readOnly = false)
    public void saveImages(Long postId, List<PostImage> postImages) {

        // 해당 postId로 Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        // PostImage 엔티티에 Post 엔티티 할당
        for (PostImage postImage : postImages) {
            postImage.setPost(post);
        }

        // PostImage 엔티티들을 저장
        postImageRepository.saveAll(postImages);

    }

//    @Transactional
//    public void saveImage(Long postId, String imagePath, String thumbPath) {
//        PostImage postImage = new PostImage();
//        postImage.setImage_detail(imagePath);
//        postImage.setImage_thumb(thumbPath);
//
//        // postId를 사용하여 해당하는 Post 엔티티를 가져와서 설정
//        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
//        postImage.setPost(post);
//
//
//        postImageRepository.save(postImage);
//    }

    @Transactional
    public Map<String, Boolean> deleteImage(Long postId) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isRemovedFromDatabase = false;

        // postId가 null이 아닌 경우에만 삭제 진행
        if (postId != null) {
            try {
                // 데이터베이스에서 이미지 정보 삭제
                postImageRepository.deleteByPost_PostId(postId);
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

    public String getKeyByPostId(Long postId) {
        Optional<PostImage> postImageOptional = postImageRepository.findByPost_PostId(postId);
        return postImageOptional.map(PostImage::getImage_thumb).orElse(null);
    }

    @Transactional
    public boolean deletePostImages(Long postId) {

        // 해당 postId로 Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        // PostImage 엔티티들을 삭제하고 S3에서도 이미지 삭제
        for (PostImage postImage : post.getImageSet()) {
            fileUploadService.deleteFile(postImage.getImage_thumb());
            postImageRepository.deleteById(postImage.getPost().getPostId());
        }

        // Post 엔티티의 이미지 목록 비우기
        post.getImageSet().clear();

        return true;

    }

//    public List<String> getImageUrlsByPostId(Long postId) throws ChangeSetPersister.NotFoundException {
//        List<String> imageUrls = new ArrayList<>();
//        List<PostImage> postImages = postImageRepository.findByPost_PostId(postId)
//        for (PostImage postImage : postImages) {
//            imageUrls.add(postImage.getImage_detail());
//        }
//        return imageUrls;
//    }

}

