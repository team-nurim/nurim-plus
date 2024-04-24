package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.TextContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextContentRepository extends JpaRepository<TextContent, Long> {
}