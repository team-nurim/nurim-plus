package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi.image_detail = :fileName")
    void deleteByFileName(String fileName);

}
