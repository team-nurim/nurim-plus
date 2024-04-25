package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.HousingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HousingPolicyRepository extends JpaRepository<HousingPolicy, Long> {

}