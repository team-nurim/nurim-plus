package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select a from Post a where a.postId =:id")
    Optional<Post> findByIdWithImages(@Param("id") Long postId);

    @Query("select p from Post p where p.postCategory = :category")
    Page<Post> findByPostCategory(@Param("category") String category, Pageable pageable);

//    @Query("SELECT i.image_detail FROM Post p JOIN p.imageSet i WHERE p.postId = :postId")
//    List<String> findByPostId(@Param("postId") Long postId);
}
