package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.community.*;
import org.nurim.nurim.domain.dto.home.ReadHomeCommunityResponse;
import org.nurim.nurim.domain.dto.reply.ReadReplyResponse;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.domain.entity.CommunityImage;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.repository.*;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class CommunityService {

    private final CommunityRepository communityRepository;

    private final MemberRepository memberRepository;

    private final ReplyService replyService;

    private final TokenProvider tokenProvider;

    /**
     * 게시물 생성 서비스
     */
    @Transactional
    public CreateCommunityResponse communityCreate(String memberEmail, CreateCommunityRequest request) {
        String token = tokenProvider.getUsernameFromToken(memberEmail);
        Member member = memberRepository.findMemberByMemberEmail(token).orElseThrow(() -> new EntityNotFoundException("작성자 id가 확인이 안됩니다 ㅜㅜ"));
        Community community = Community.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .communityCategory(request.getCategory())
                .build();

        Community saveCommunity = communityRepository.save(community);

        return new CreateCommunityResponse(
                saveCommunity.getCommunityId(),
                saveCommunity.getMember().getMemberId(),
                saveCommunity.getTitle(),
                saveCommunity.getContent(),
                saveCommunity.getRegisterDate(),
                saveCommunity.getCommunityCategory());
    }

    /**
     * 게시물 단일 조회 및 조회수,댓글출력 서비스
     */

    @Transactional
    public ReadCommunityResponse communityRead(Long communityId) {

        Community findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 communityId로 조회된 게시글이 없습니다."));
        log.info("커뮤니티 조회 성공");

        List<ReadReplyResponse> listReply = replyService.getRepliesByCommunityId(communityId);//댓글 서비스를 가져와 커뮤니티 아이디당 찾는 댓글 리스트를 게시글과 같이 조회한다.

        List<String> imageUrls = findCommunity.getCommunityImage().stream()
                        .map(CommunityImage::getFilePath)
                        .collect(Collectors.toList());

        List<Long> communityImageId = findCommunity.getCommunityImage().stream()
                .map(CommunityImage::getCommunityImageId)
                .collect(Collectors.toList());


        communityRepository.updateCount(communityId);
        return new ReadCommunityResponse(
                findCommunity.getCommunityId(),
                imageUrls,
                communityImageId,
                findCommunity.getTitle(),
                findCommunity.getContent(),
                findCommunity.getCommunityCategory(),
                findCommunity.getRegisterDate(),
                findCommunity.getModifyDate(),
                findCommunity.getViewCounts(),
                findCommunity.getRecommend(),
                findCommunity.getMember().getMemberNickname(),
                findCommunity.getMember().getMemberEmail(),
                listReply);
    }

    /**
     *게시글 삭제 글쓴이 본인만!
     */
    @Transactional
    public DeleteCommunityResponse communityDelete(Long communityId, String memberEmail) throws AccessDeniedException {
        String accessToken = tokenProvider.getUsernameFromToken(memberEmail);
        Community findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 communityId로 조회된 게시글이 없습니다."));

        Member member = memberRepository.findMemberByMemberEmail(accessToken)
                .orElseThrow(() -> new EntityNotFoundException("작성자 id가 확인이 안됩니다 ㅜㅜ"));

        if (Objects.equals(findCommunity.getMember().getMemberEmail(), member.getMemberEmail())) {
            communityRepository.delete(findCommunity);
            return new DeleteCommunityResponse(findCommunity.getCommunityId());
        } else {
            throw new AccessDeniedException("커뮤니티를 삭제할 권한이 없습니다.");
        }
    }

    /**
     * 게시글 수정 글쓴이 본인만!
     */

    @Transactional
    public UpdateCommunityResponse communityUpdate(Long communityId, String memberEmail, UpdateCommunityRequest request) throws AccessDeniedException {
        String accessToken = tokenProvider.getUsernameFromToken(memberEmail);
        Community findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 아이디로 조회안됨"));
        Member member = memberRepository.findMemberByMemberEmail(accessToken)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일로 조회안됨"));

        if(Objects.equals(member.getMemberEmail(),findCommunity.getMember().getMemberEmail() )) {

            findCommunity.update(request.getTitle(), request.getContent());

            return new UpdateCommunityResponse(
                    findCommunity.getTitle(),
                    findCommunity.getContent(),
                    findCommunity.getModifyDate());
        }else {
            throw new AccessDeniedException("커뮤니티를 수정할 권한이 없습니다.");
        }
    }
    public Page<ReadAllCommunityResponse> getCommunityList(Pageable pageable){

        Page<Community> communities = communityRepository.findAll(pageable);
        return communities.map(community -> {
            List<String> imageUrls = community.getCommunityImage().stream()
                    .map(CommunityImage::getFilePath)
                    .collect(Collectors.toList());

            return new ReadAllCommunityResponse(
                    community.getCommunityId(),
                    imageUrls,
                    community.getTitle(),
                    community.getContent(),
                    community.getCommunityCategory(),
                    community.getRegisterDate(),
                    community.getViewCounts(),
                    community.getRecommend(),
                    community.getMember().getMemberNickname());
        });
    }


    public Page<ReadSearchResponse> getCommunityListByCategory(String category, Pageable pageable) {
        Page<Community> communityPage = communityRepository.findByCommunityCategory(category, pageable);

        return communityPage.map(community -> {
            Long memberId = community.getMember().getMemberId();

            List<String> imageUrls = community.getCommunityImage().stream()
                    .map(CommunityImage::getFilePath)
                    .collect(Collectors.toList());

            return new ReadSearchResponse(
                    community.getCommunityId(),
                    imageUrls,
                    community.getTitle(),
                    community.getContent(),
                    community.getCommunityCategory(),
                    community.getRegisterDate(),
                    community.getModifyDate(),
                    community.getViewCounts(),
                    community.getRecommend(),
                    community.getMember().getMemberNickname());
        });
    }

    /**
     *조회수 많은 게시글 5개
     */
    public Page<ReadCountsCommunitiesResponse> findPopularCommunities(Pageable pageable) {
        Page<Community> popularPage = communityRepository.findAll(pageable);

        return popularPage.map(community -> {
            Long memberId = community.getMember().getMemberId();

            return new ReadCountsCommunitiesResponse(
                    community.getCommunityId(),
                    community.getTitle(),
                    community.getContent(),
                    community.getViewCounts(),
                    community.getRecommend(),
                    community.getRegisterDate(),
                    community.getMember().getMemberNickname());
        });
    }

    public Page<ReadSearchResponse> SearchTitleAndCategoryAndMemberNickName(String communityTitle,String communityCategory,String memberNickname, Pageable pageable) {
        Page<Community> searchPage = communityRepository.findByTitleAndCommunityCategoryAndMember_MemberNickname(communityTitle,communityCategory,memberNickname, pageable);

        return searchPage.map(community -> {
            Long memberId = community.getMember().getMemberId();

            List<String> imageUrls = community.getCommunityImage().stream()
                    .map(CommunityImage::getFilePath)
                    .collect(Collectors.toList());

            return new ReadSearchResponse(
                    community.getCommunityId(),
                    imageUrls,
                    community.getTitle(),
                    community.getContent(),
                    community.getCommunityCategory(),
                    community.getRegisterDate(),
                    community.getModifyDate(),
                    community.getViewCounts(),
                    community.getRecommend(),
                    community.getMember().getMemberNickname());
        });

    }
    public Page<ReadSearchResponse> SearchTitle(String keyword,Pageable pageable) {
        Page<Community> searchPage = communityRepository.findByTitleContaining(keyword,pageable);

        return searchPage.map(community -> {
            Long memberId = community.getMember().getMemberId();

            List<String> imageUrls = community.getCommunityImage().stream()
                    .map(CommunityImage::getFilePath)
                    .collect(Collectors.toList());

            return new ReadSearchResponse(
                    community.getCommunityId(),
                    imageUrls,
                    community.getTitle(),
                    community.getContent(),
                    community.getCommunityCategory(),
                    community.getRegisterDate(),
                    community.getModifyDate(),
                    community.getViewCounts(),
                    community.getRecommend(),
                    community.getMember().getMemberNickname());
        });

    }
    public Page<ReadSearchResponse> SearchCategory(String communityCategory, Pageable pageable) {
        Page<Community> searchPage = communityRepository.findByCommunityCategory(communityCategory, pageable);

        return searchPage.map(community -> {
            Long memberId = community.getMember().getMemberId();

            List<String> imageUrls = community.getCommunityImage().stream()
                    .map(CommunityImage::getFilePath)
                    .collect(Collectors.toList());

            return new ReadSearchResponse(
                    community.getCommunityId(),
                    imageUrls,
                    community.getTitle(),
                    community.getContent(),
                    community.getCommunityCategory(),
                    community.getRegisterDate(),
                    community.getModifyDate(),
                    community.getViewCounts(),
                    community.getRecommend(),
                    community.getMember().getMemberNickname());
        });

    }

    public Page<ReadSearchResponse> SearchMemberNickName(String memberNickname, Pageable pageable) {
        Page<Community> searchPage = communityRepository.findByMemberMemberNickname(memberNickname, pageable);

        return searchPage.map(community -> {
            Long memberId = community.getMember().getMemberId();

            List<String> imageUrls = community.getCommunityImage().stream()
                    .map(CommunityImage::getFilePath)
                    .collect(Collectors.toList());

            return new ReadSearchResponse(
                    community.getCommunityId(),
                    imageUrls,
                    community.getTitle(),
                    community.getContent(),
                    community.getCommunityCategory(),
                    community.getRegisterDate(),
                    community.getModifyDate(),
                    community.getViewCounts(),
                    community.getRecommend(),
                    community.getMember().getMemberNickname());
        });

    }
    public Page<ReadInquireResponse> NoRepliesButHit(Pageable pageable){
        Page<Community> InquirePage = communityRepository.findCommunitiesWithNoRepliesOrderByViewCountsDesc(pageable);
        return InquirePage.map(community -> {
            Long memberId = community.getMember().getMemberId();

            return new ReadInquireResponse(
                    community.getCommunityId(),
                    community.getTitle(),
                    community.getCommunityCategory(),
                    community.getContent(),
                    community.getViewCounts(),
                    community.getRegisterDate());
        });
    }
    


}
