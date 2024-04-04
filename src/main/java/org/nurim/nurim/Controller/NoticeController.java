package org.nurim.nurim.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.notice.*;
import org.nurim.nurim.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Log4j2
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/notice/register")
    public ResponseEntity<CreateNoticeResponse> noticeCreate(@RequestBody CreateNoticeRequest request) {


        CreateNoticeResponse response = noticeService.createNotice(request);

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

    @GetMapping("/notice")
    public ResponseEntity<Page<ReadNoticeResponse>> noticeReadAll(@PageableDefault(
            size = 5, sort = "noticeId", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReadNoticeResponse>  response = noticeService.readAllNotice(pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
