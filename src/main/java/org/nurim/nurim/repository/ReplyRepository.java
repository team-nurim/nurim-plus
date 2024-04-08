package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("SELECT r FROM Reply r WHERE r.community.communityId = :communityId")
    List<Reply> findByCommunityCommunityId(@Param("communityId") Long communityId);
}
