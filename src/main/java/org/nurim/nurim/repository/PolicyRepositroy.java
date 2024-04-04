package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepositroy extends JpaRepository<Policy, Long> {
}
