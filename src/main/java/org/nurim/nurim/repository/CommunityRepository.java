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

    Page<Community> findByCommunityCategory(String communityCategory, Pageable pageable); //카테고리별 페이징 처리

    @Modifying
    @Query("update Community c set c.viewCounts = coalesce(c.viewCounts, 0) + 1 where c.communityId = :communityId")
    int updateCount(@Param("communityId") Long communityId);//조회수 올라감

    Page<Community> findByTitleAndCommunityCategoryAndMember_MemberNickname(String communityTitle,String communityCategory,String memberNickname ,Pageable pageable);

    Page<Community> findByTitle(String communityTitle,Pageable pageable);

    Page<Community> findByMemberMemberNickname(String memberNickname, Pageable pageable);

//    Page<Community> findByTitleAndMemberMemberNickname(String communityTitle, String memberNickname, Pageable pageable);

    Optional<Community> findByCommunityIdAndMember_MemberId(Long communityId, Long member_memberId);


}
