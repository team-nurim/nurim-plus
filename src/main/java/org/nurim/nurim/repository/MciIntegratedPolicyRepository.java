package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.MciIntegratedPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MciIntegratedPolicyRepository extends JpaRepository<MciIntegratedPolicy, Long>, JpaSpecificationExecutor<MciIntegratedPolicy> {

    // 지역과 키워드에 따른 검색
    Page<MciIntegratedPolicy> findByRegionAndSupportDetailsContaining(String region, String keyword, Pageable pageable);

    // 지역에 따른 검색
    Page<MciIntegratedPolicy> findByRegion(String region, Pageable pageable);
}