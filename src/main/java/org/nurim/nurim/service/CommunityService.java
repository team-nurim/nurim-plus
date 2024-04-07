package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.community.*;
import org.nurim.nurim.domain.dto.reply.ReadReplyResponse;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.domain.entity.CommunityImage;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.Recommend;
import org.nurim.nurim.repository.CommunityImageRepository;
import org.nurim.nurim.repository.CommunityRepository;
import org.nurim.nurim.repository.MemberRepository;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.nurim.nurim.repository.RecommendRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    private final MemberRepository memberRepository;

    private final CommunityImageRepository communityImageRepository;

    private final  RecommendRepository recommendRepository;

    @Transactional
    public CreateCommunityResponse communityCreate(Long memberId, CreateCommunityRequest request) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new EntityNotFoundException("확인안됨"));
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

    @Transactional
    public ReadCommunityResponse communityRead(Long communityId) {
        Community findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 communityId로 조회된 게시글이 없습니다."));
        communityRepository.updateCount(communityId);
        return new ReadCommunityResponse(
                findCommunity.getCommunityId(),
                findCommunity.getCommunityImage(),
                findCommunity.getTitle(),
                findCommunity.getContent(),
                findCommunity.getCommunityCategory(),
                findCommunity.getRegisterDate(),
                findCommunity.getModifyDate(),
                findCommunity.getCounts(),
                findCommunity.getRecommend());
    }

    @Transactional
    public DeleteCommunityResponse communityDelete(Long communityId) {
        Community findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 communityId로 조회된 게시글이 없습니다."));

        communityRepository.delete(findCommunity);

        return new DeleteCommunityResponse(findCommunity.getCommunityId());
        }

    @Transactional
    public UpdateCommunityResponse communityUpdate(Long communityId, UpdateCommunityRequest request){

        Community findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 아이디로 조회안됨"));

        findCommunity.update(request.getTitle(),request.getContent(),request.getCategory());

        return new UpdateCommunityResponse(
                findCommunity.getTitle(),
                findCommunity.getContent(),
                findCommunity.getModifyDate(),
                findCommunity.getCommunityCategory());
    }
    public Page<ReadCommunityResponse> getCommunityListByCategory(String category, Pageable pageable){
        Page<Community> communityPage = communityRepository.findByCommunityCategory(category, pageable);

        return communityPage.map(community -> {
            Long memberId = community.getMember().getMemberId();

            return new ReadCommunityResponse(
                    community.getCommunityId(),
                    community.getCommunityImage(),
                    community.getTitle(),
                    community.getContent(),
                    community.getCommunityCategory(),
                    community.getRegisterDate(),
                    community.getModifyDate(),
                    community.getCounts(),
                    community.getRecommend());
        });
    }

    @Transactional
    public int updateCount(Long communityId){
        return communityRepository.updateCount(communityId);
    }

}

