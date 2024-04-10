package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    @Transactional
    @Modifying
    @Query("delete from PostImage pi where pi.postImageId= :postImageId")
    void deleteByFileName(@Param("postImageId") Long postImageId);
}