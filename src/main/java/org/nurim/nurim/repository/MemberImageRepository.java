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
    @Query("delete from MemberImage mi where mi.memberProfileImage = :fileName")
    void deleteByFileName(@Param("fileName") String fileName);

//    @Transactional
//    @Modifying
//    @Query("update MemberImage mi set mi.memberProfileImage = '디폴트 이미지 경로' where mi.memberProfileImage =: fileName")
//    void updateMemberImageByFileName(@Param("fileName") String fileName);

//    @Query("delete from MemberImage mi where mi.member.memberId = :memberId")
//    void deleteByMember_MemberId(@Param("memberId") Long memberId);

    Optional<MemberImage> findByMember_MemberId(Long memberId);
}
