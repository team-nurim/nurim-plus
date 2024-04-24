package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {


    Page<Community> findByCommunityCategory(String communityCategory, Pageable pageable); //카테고리별 페이징 처리(검색)

    @Modifying
    @Query("update Community c set c.viewCounts = coalesce(c.viewCounts, 0) + 1 where c.communityId = :communityId")
    int updateCount(@Param("communityId") Long communityId);//조회수 올라가게 하는 쿼리

    Page<Community> findByTitleAndCommunityCategoryAndMember_MemberNickname(String communityTitle,String communityCategory,String memberNickname ,Pageable pageable);
    //전체 키워드 찾기

    Page<Community> findByTitle(String communityTitle,Pageable pageable);
    //제목에 따른 검색

    Page<Community> findByMemberMemberNickname(String memberNickname, Pageable pageable);
    //닉네임에 따른 검색

    @Query("SELECT c FROM Community c WHERE c.communityId NOT IN (SELECT r.community.communityId FROM Reply r) ORDER BY c.viewCounts DESC")
    Page<Community> findCommunitiesWithNoRepliesOrderByViewCountsDesc(Pageable pageable);//댓글이 없는 게시물 조회 쿼리

    @Query("SELECT m.memberEmail FROM Community c JOIN c.member m WHERE c.communityId = :communityId")
    Optional<String> findMemberEmailByCommunityId(@Param("communityId") Long communityId);

}
