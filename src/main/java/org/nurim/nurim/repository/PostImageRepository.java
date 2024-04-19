package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.domain.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    @Transactional
    @Modifying
    @Query("delete from PostImage pi where pi.postImageId= :postImageId")
    void deleteByFileName(@Param("postImageId") Long postImageId);

    void deleteByPost_PostId(Long postId);

    Optional<PostImage> findByPost_PostId(Long postId);

}