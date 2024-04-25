package org.nurim.nurim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EligibilityForAHappyHomeRepository extends JpaRepository<EligibilityForAHappyHome, Long> {

}