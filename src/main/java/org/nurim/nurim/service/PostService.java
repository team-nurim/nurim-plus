package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.post.*;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.Notice;
import org.nurim.nurim.domain.entity.Post;
import org.nurim.nurim.domain.entity.PostImage;
import org.nurim.nurim.repository.MemberRepository;
import org.nurim.nurim.repository.PostImageRepository;
import org.nurim.nurim.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CreatePostResponse createPost(Long memberId, CreatePostRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Admin with ID " + memberId + " not found"));

        Post post = Post.builder()
                .postTitle(request.getPostTitle())
                .postContent(request.getPostContent())
                .postWriter(request.getPostWriter())
                .postCategory(request.getPostCategory())
                .postRegisterDate(request.getPostRegisterDate())
                .member(member)// Admin 객체를 Post 객체의 admin 필드에 할당합니다.
                .build();

//            request.getFileNames().forEach(fileName -> {
//                String[] arr = fileName.split("_");
//                if(arr.length > 1){post.addPostImage(arr[0], arr[1]);
//                }
//            });


        Post savedPost = postRepository.save(post);

//        List<String> fileNames = savedPost.getImageSet().stream()
//                .sorted()
//                .map(postImage -> postImage.getImage_detail() + "_" + postImage.getImage_thumb())
//                .collect(Collectors.toList());


        return new CreatePostResponse(
                savedPost.getPostId(),
                savedPost.getPostTitle(),
                savedPost.getPostContent(),
                savedPost.getPostWriter(),
                savedPost.getPostCategory(),
                savedPost.getPostRegisterDate()
//                fileNames

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

    public Page<ReadPostResponse> readPostsByKeywordAndCategory(String category, Pageable pageable) {
        Page<Post> postsPage = postRepository.findByPostCategory(category, pageable);
        return postsPage.map(post -> new ReadPostResponse(post.getPostId(), post.getPostTitle(), post.getPostContent(),
                post.getPostWriter(), post.getPostCategory(), post.getPostRegisterDate()));
    }

}
