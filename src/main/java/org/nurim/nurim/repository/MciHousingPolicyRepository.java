package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.MciHousingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MciHousingPolicyRepository extends JpaRepository<MciHousingPolicy, Long>, JpaSpecificationExecutor<MciHousingPolicy> {

}
