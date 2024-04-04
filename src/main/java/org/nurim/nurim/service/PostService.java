package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.notice.*;
import org.nurim.nurim.domain.dto.post.*;
import org.nurim.nurim.domain.entity.Admin;
import org.nurim.nurim.domain.entity.Notice;
import org.nurim.nurim.domain.entity.Post;
import org.nurim.nurim.repository.AdminRepository;
import org.nurim.nurim.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class PostService {

    private final PostRepository postRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public CreatePostResponse createPost(Long adminId, CreatePostRequest request) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin with ID " + adminId + " not found"));

        Post post = Post.builder()
                .postTitle(request.getPostTitle())
                .postContent(request.getPostContent())
                .postWriter(request.getPostWriter())
                .postCategory(request.getPostCategory())
                .postRegisterDate(request.getPostRegisterDate())
                .admin(admin)// Admin 객체를 Post 객체의 admin 필드에 할당합니다.
                .build();


        Post savedPost = postRepository.save(post);


        return new CreatePostResponse(
                savedPost.getPostId(),
                savedPost.getPostTitle(),
                savedPost.getPostContent(),
                savedPost.getPostWriter(),
                savedPost.getPostCategory(),
                savedPost.getPostRegisterDate()

        );
    }
    public ReadPostResponse readPostById(Long postId) {

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 postId로 조회된 게시글이 없습니다."));

        return new ReadPostResponse(foundPost.getPostId(),
                foundPost.getPostTitle(),
                foundPost.getPostContent(),
                foundPost.getPostWriter(),
                foundPost.getPostCategory(),
                foundPost.getPostRegisterDate());
    }
    @Transactional
    public UpdatePostResponse updatePost(Long postId, UpdatePostRequest request) {

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 postId로 조회된 게시글이 없습니다."));
        //Dirty Checking
        foundPost.update(request.getPostTitle(),
                request.getPostContent(),
                request.getPostWriter(),
                request.getPostCategory(),
                request.getPostRegisterDate());

        return new UpdatePostResponse(foundPost.getPostId(),
                foundPost.getPostTitle(),
                foundPost.getPostContent(),
                foundPost.getPostWriter(),
                foundPost.getPostCategory(),
                foundPost.getPostRegisterDate());

    }

    @Transactional
    public DeletePostResponse deletePost(Long postId) {

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 noticeId로 조회된 게시글이 없습니다."));

        postRepository.delete(foundPost);

        return new DeletePostResponse(foundPost.getPostId());
//                foundnotice.getNoticeTitle(),
//                foundnotice.getNoticeContent(),
//                foundnotice.getNoticeWriter(),
//                foundnotice.getNoticeRegisterDate());

    }

    public Page<ReadPostResponse> readAllPost(Pageable pageable) {

        Page<Post> postsPage = postRepository.findAll(pageable);

        return postsPage.map(post -> new ReadPostResponse(post.getPostId(),post.getPostTitle(),post.getPostContent(),
                post.getPostWriter(), post.getPostCategory(), post.getPostRegisterDate()));
    }

}
