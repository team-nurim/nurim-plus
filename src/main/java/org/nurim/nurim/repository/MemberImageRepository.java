package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.MemberImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {
    @Transactional
    @Modifying
    @Query("delete from MemberImage mi where mi.memberProfileImage = :fileName")
    void deleteByFileName(@Param("fileName") String fileName);
}
