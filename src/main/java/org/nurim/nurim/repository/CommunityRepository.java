package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Page<Community> findByCommunityCategory(String communityCategory, Pageable pageable); //카테고리별 페이징 처리

    @Modifying
    @Query("update Community c set c.counts = coalesce(c.counts, 0) + 1 where c.communityId = :communityId")
    int updateCount(@Param("communityId") Long communityId);


}
