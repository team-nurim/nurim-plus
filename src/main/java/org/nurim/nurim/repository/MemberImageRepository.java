package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.MemberImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {
    @Transactional
    @Modifying
    @Query("delete from MemberImage mi where mi.member.memberId =:memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Transactional
    @Modifying
    @Query("update MemberImage mi set mi.memberProfileImage = :uuid where mi.member.memberId = :memberId")
    void updateByMemberId(@Param("uuid") String uuid, @Param("memberId")Long memberId);

    Optional<MemberImage> findByMember_MemberId(Long memberId);
}
