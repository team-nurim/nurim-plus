package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ExpertRepository extends JpaRepository<Expert, Long> {

    @Transactional
    @Modifying
    @Query("delete from Expert ei where ei.expertFile = :fileName")
    void deleteByFileName(@Param("fileName") String fileName);

}
