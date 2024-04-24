package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.MciIntegratedPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MciIntegratedPolicyRepository extends JpaRepository<MciIntegratedPolicy, Long>, JpaSpecificationExecutor<MciIntegratedPolicy> {
}

