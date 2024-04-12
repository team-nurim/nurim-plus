package org.nurim.nurim.repository;

import org.nurim.nurim.domain.entity.Notice;
import org.nurim.nurim.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByNoticeTitleContainingOrNoticeContentContaining(String keyword1, String keyword2, Pageable pageable);

}
