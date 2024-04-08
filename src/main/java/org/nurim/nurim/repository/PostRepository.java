package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select a from Post a where a.postId =:id")
    Optional<Post> findByIdWithImages(@Param("id") Long postId);

    Page<Post> findByPostTitleContainingOrPostContentContaining(String keyword1, String keyword2, Pageable pageable);


}
