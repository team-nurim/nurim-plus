package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.Recommend;
import org.nurim.nurim.repository.CommunityRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.nurim.nurim.repository.RecommendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class HeartService {

    private final RecommendRepository recommendRepository;
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final TokenProvider tokenProvider;

    public void recommendCommunity(Long communityId, String memberEmail) {

        String token = tokenProvider.getUsernameFromToken(memberEmail);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("커뮤니티를 찾을 수 없습니다."));
        Member member = memberRepository.findMemberByMemberEmail(token)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Optional<Recommend> existingRecommend = recommendRepository.findByCommunityAndMember(community, member);
        if (existingRecommend.isPresent()) {
            return; // 이미 좋아요를 누른 경우 종료
        }

        Recommend newRecommend = new Recommend(community, member);
        recommendRepository.save(newRecommend);

        Long recommendCount = community.getRecommend() != null ? community.getRecommend() : 0L;
        community.setRecommend(recommendCount + 1);
        communityRepository.save(community);
    }
    public void cancelRecommendCommunity(Long communityId, String memberEmail) {

        String token = tokenProvider.getUsernameFromToken(memberEmail);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("커뮤니티를 찾을 수 없습니다."));
        Member member = memberRepository.findMemberByMemberEmail(token)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Optional<Recommend> existingRecommend = recommendRepository.findByCommunityAndMember(community, member);
        if (existingRecommend.isPresent()) {
            recommendRepository.delete(existingRecommend.get());
            Long recommendCount = community.getRecommend() != null ? community.getRecommend() : 0L;
            community.setRecommend(recommendCount - 1);
            communityRepository.save(community);
        } else {
            throw new IllegalStateException("이미 좋아요를 취소했습니다.");
        }
    }
    public boolean isCommunityLiked(Long communityId, String memberEmail) {
        String token = tokenProvider.getUsernameFromToken(memberEmail);
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("커뮤니티를 찾을 수 없습니다."));
        Member member = memberRepository.findMemberByMemberEmail(token)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        return recommendRepository.findByCommunityAndMember(community, member).isPresent();
    }
}



