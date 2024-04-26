package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.MciHousingPolicy;
import org.nurim.nurim.domain.entity.api.ChildCare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MciHousingPolicyRepository extends JpaRepository<MciHousingPolicy, Long>, JpaSpecificationExecutor<MciHousingPolicy> {

    // 지역과 키워드에 따른 검색
    Page<MciHousingPolicy> findByRegionAndSupportDetailsContaining(String region, String keyword, Pageable pageable);

    // 지역에 따른 검색
    Page<MciHousingPolicy> findByRegion(String region, Pageable pageable);
}