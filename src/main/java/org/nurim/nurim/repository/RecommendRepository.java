package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.Recommend;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    Optional<Recommend> findByCommunityAndMember(Community community, Member member);

}
