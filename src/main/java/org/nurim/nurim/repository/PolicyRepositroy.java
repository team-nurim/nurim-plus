package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

// 이 리포지토리는 :스프링데이터 JPA를 사용해서, 데이터베이스 작업을 추상화하기위해 사용!
public interface PolicyRepositroy extends JpaRepository<Policy, Long> {
}
