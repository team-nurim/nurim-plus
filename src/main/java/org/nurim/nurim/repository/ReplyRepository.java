package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByCommunityCommunityId(Long communityId);
}
