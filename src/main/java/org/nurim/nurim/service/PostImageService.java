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

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository; // PostRepository 추가
    private final FileUploadService fileUploadService;

    @Transactional
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

    @Transactional
    public boolean deletePostImages(Long postImageId) {

        // 해당 postImageId로 PostImage 엔티티 조회
        PostImage postImage = postImageRepository.findById(postImageId)
                .orElseThrow(() -> new EntityNotFoundException("Post Image not found with id: " + postImageId));

        // S3에서 이미지 삭제
        fileUploadService.deleteFile(postImage.getImage_thumb());

        // PostImage 엔티티 삭제
        postImageRepository.deleteById(postImageId);

        return true;
    }

//    @Transactional
//    public boolean deletePostImages(Long postImageId) {
//
//        // 해당 postId로 Post 엔티티 조회
//        Post post = postRepository.findById(postImageId)
//                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postImageId));
//
//        // PostImage 엔티티들을 삭제하고 S3에서도 이미지 삭제
//        for (PostImage postImage : post.getImageSet()) {
//            fileUploadService.deleteFile(postImage.getImage_thumb());
//            postImageRepository.deleteById(postImage.getPost().getPostId());
//        }
//
//        // Post 엔티티의 이미지 목록 비우기
//        post.getImageSet().clear();
//
//        return true;
//
//    }

}

