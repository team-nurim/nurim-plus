package org.nurim.nurim.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.notice.*;
import org.nurim.nurim.domain.dto.post.ReadPostResponse;
import org.nurim.nurim.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Log4j2
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/notice/register/{adminId}")
    public ResponseEntity<CreateNoticeResponse> noticeCreate(@PathVariable Long adminId, @RequestBody CreateNoticeRequest request) {

        CreateNoticeResponse response = noticeService.createNotice(adminId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/notice/read{noticeId}")
    public ResponseEntity<ReadNoticeResponse> noticeRead(@PathVariable Long noticeId) {

        ReadNoticeResponse response = noticeService.readNoticeById(noticeId);

        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/notice/update{noticeId}")
    public ResponseEntity<UpdateNoticeResponse> postUpdate(@PathVariable Long noticeId,
                                                           @RequestBody UpdateNoticeRequest request){

        UpdateNoticeResponse response = noticeService.updateNotice(noticeId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity<DeleteNoticeResponse> postDelete(@PathVariable Long noticeId) {

        DeleteNoticeResponse response = noticeService.deleteNotice(noticeId);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/notice/list")
    public ResponseEntity<List<ReadNoticeResponse>> noticeReadAll() {

        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("noticeId").descending());

        Page<ReadNoticeResponse> createNotice = noticeService.readAllNotice(pageRequest);

        List<ReadNoticeResponse> createNoticeList = createNotice.getContent();

        return new ResponseEntity<>(createNoticeList, HttpStatus.OK);
    }

    @GetMapping("/notice/search")
    public Page<ReadNoticeResponse> readNoticeByKeyword(@RequestParam String keyword, Pageable pageable) {
        // 키워드로 게시물 검색
        return noticeService.readNoticeByKeyword(keyword, pageable);
    }


}
