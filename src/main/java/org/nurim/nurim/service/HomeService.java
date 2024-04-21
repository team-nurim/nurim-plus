package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.home.ReadHomePostResponse;
import org.nurim.nurim.domain.dto.post.ReadPostResponse;
import org.nurim.nurim.domain.entity.Post;
import org.nurim.nurim.domain.entity.PostImage;
import org.nurim.nurim.repository.HomeRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.nurim.nurim.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class HomeService {

    private final HomeRepository homeRepository;
    private final MemberRepository memberRepository;

    public ReadHomePostResponse readHomePostById(Long postId) {

        Post foundPost = homeRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 postId로 조회된 게시글이 없습니다."));

        String firstImage = null;
        Set<PostImage> imageSet = foundPost.getImageSet();
        if (imageSet != null && !imageSet.isEmpty()) {
            // Set에서 첫 번째 이미지를 가져옴
            for (PostImage image : imageSet) {
                firstImage = image.getImage_detail();
                break;
            }
        }

        return new ReadHomePostResponse(
                foundPost.getPostId(),
                foundPost.getPostTitle(),
                foundPost.getPostContent(),
                foundPost.getPostWriter(),
                foundPost.getPostCategory(),
                foundPost.getPostRegisterDate(),
                firstImage
        );
    }

    public Page<ReadHomePostResponse> readAllHomePost(Pageable pageable) {

        Page<Post> postsPage = homeRepository.findAll(pageable);

        return postsPage.map(post -> {

            String firstImage = null;
            Set<PostImage> imageSet = post.getImageSet();

            if (imageSet != null && !imageSet.isEmpty()) {
                for (PostImage image : imageSet) {
                    firstImage = image.getImage_detail();
                    break;
                }
            }

            return new ReadHomePostResponse(
                    post.getPostId(),
                    post.getPostTitle(),
                    post.getPostContent(),
                    post.getPostWriter(),
                    post.getPostCategory(),
                    post.getPostRegisterDate(),
                    firstImage
            );
        });
    }

}
