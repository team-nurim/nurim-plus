package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.home.ReadHomeCommunityResponse;
import org.nurim.nurim.domain.dto.home.ReadHomeCountsCommunitiesResponse;
import org.nurim.nurim.domain.dto.home.ReadHomePostResponse;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.domain.entity.Post;
import org.nurim.nurim.domain.entity.PostImage;
import org.nurim.nurim.repository.CommunityRepository;
import org.nurim.nurim.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class HomeService {

    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;

    public ReadHomePostResponse readHomePostById(Long postId) {

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 postId로 조회된 게시글이 없습니다."));

        String firstImage = null;
        List<PostImage> imageSet = foundPost.getImageSet();
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

        Page<Post> postsPage = postRepository.findAll(pageable);

        return postsPage.map(post -> {

            String firstImage = null;
            List<PostImage> imageSet = post.getImageSet();

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

    // 커뮤니티 등록순 리스트
    public Page<ReadHomeCommunityResponse> getHomeCommunityList(Pageable pageable){

        Page<Community> communities = communityRepository.findAll(pageable);

        return communities.map(community -> {
            Long memberId = community.getMember().getMemberId();

            return new ReadHomeCommunityResponse(
                    community.getCommunityId(),
                    community.getTitle(),
                    community.getContent(),
                    community.getCommunityCategory(),
                    community.getRegisterDate(),
                    community.getModifyDate(),
                    community.getViewCounts(),
                    community.getRecommend(),
                    community.getMember().getMemberNickname(),
                    community.getMember().getMemberImage().getMemberProfileImage());
        });
    }


    // 조회수 많은 커뮤니티 게시글 5개
    public Page<ReadHomeCountsCommunitiesResponse> findPopularCommunities(Pageable pageable) {
        Page<Community> popularPage = communityRepository.findAll(pageable);

        return popularPage.map(community -> {
            Long memberId = community.getMember().getMemberId();

            String memberProfileImage = community.getMember().getMemberImage().getMemberProfileImage();

            return new ReadHomeCountsCommunitiesResponse(
                    community.getTitle(),
                    community.getContent(),
                    community.getViewCounts(),
                    community.getRecommend(),
                    community.getRegisterDate(),
                    community.getMember().getMemberNickname(),
                    memberProfileImage
            );
        });
    }

}
