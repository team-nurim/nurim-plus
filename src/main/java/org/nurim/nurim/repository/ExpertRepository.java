package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Expert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertRepository extends JpaRepository<Expert, Long> {
}
