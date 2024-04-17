package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.MemberImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {
    Optional<MemberImage> findByMember_MemberId(Long memberId);

}
