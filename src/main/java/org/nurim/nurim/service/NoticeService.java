package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.notice.*;
import org.nurim.nurim.domain.entity.Notice;
import org.nurim.nurim.repository.NoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public CreateNoticeResponse createNotice(CreateNoticeRequest request) {

        Notice notice = Notice.builder()
                .noticeTitle(request.getNoticeTitle())
                .noticeContent(request.getNoticeContent())
                .noticeWriter(request.getNoticeWriter())
                .noticeRegisterDate(request.getNoticeRegisterDate())
                .build();



        Notice savedNotice = noticeRepository.save(notice);


        return new CreateNoticeResponse(
                savedNotice.getNoticeId(),
                savedNotice.getNoticeTitle(),
                savedNotice.getNoticeContent(),
                savedNotice.getNoticeWriter(),
                savedNotice.getNoticeRegisterDate()

        );
    }
    public ReadNoticeResponse readNoticeById(Long noticeId) {

        Notice foundNotice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 noticeId로 조회된 게시글이 없습니다."));

        return new ReadNoticeResponse(foundNotice.getNoticeId(),
                foundNotice.getNoticeTitle(),
                foundNotice.getNoticeContent(),
                foundNotice.getNoticeWriter(),
                foundNotice.getNoticeRegisterDate());
    }
    @Transactional
    public UpdateNoticeResponse updateNotice(Long noticeId, UpdateNoticeRequest request) {

        Notice foundNotice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 noticeId로 조회된 게시글이 없습니다."));
        //Dirty Checking
        foundNotice.update(request.getNoticeTitle(), request.getNoticeContent(), request.getNoticeWriter());

        return new UpdateNoticeResponse(foundNotice.getNoticeId(),
                foundNotice.getNoticeTitle(),
                foundNotice.getNoticeContent(),
                foundNotice.getNoticeWriter(),
                foundNotice.getNoticeRegisterDate());

    }

    @Transactional
    public DeleteNoticeResponse deleteNotice(Long noticeId) {

        Notice foundnotice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 noticeId로 조회된 게시글이 없습니다."));

        noticeRepository.delete(foundnotice);

        return new DeleteNoticeResponse(foundnotice.getNoticeId());
//                foundnotice.getNoticeTitle(),
//                foundnotice.getNoticeContent(),
//                foundnotice.getNoticeWriter(),
//                foundnotice.getNoticeRegisterDate());

    }

    public Page<ReadNoticeResponse> readAllNotice(Pageable pageable) {

        Page<Notice> postsPage = noticeRepository.findAll(pageable);

        return postsPage.map(notice -> new ReadNoticeResponse(notice.getNoticeId(),notice.getNoticeTitle(),notice.getNoticeContent(),
                notice.getNoticeWriter(), notice.getNoticeRegisterDate()));
    }



}