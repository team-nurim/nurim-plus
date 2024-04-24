package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.api.ChildCare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildCareRepository extends JpaRepository<ChildCare, Long> {

    // 지역과 키워드에 따른 검색
    Page<ChildCare> findBySigunNmContainingAndPaymentContaining(String region, String keyword, Pageable pageable);

    // 지역에 따른 검색
    Page<ChildCare> findBySigunNmContaining(String region, Pageable pageable);
}

