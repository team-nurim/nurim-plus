package org.nurim.nurim.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.home.ReadHomePostResponse;
import org.nurim.nurim.service.HomeService;
import org.nurim.nurim.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Log4j2
public class HomeController {

    private final HomeService homeService;


    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/read/{postId}")
    public ResponseEntity<ReadHomePostResponse> postRead(@PathVariable Long postId) {

        ReadHomePostResponse response = homeService.readHomePostById(postId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/list")
    public ResponseEntity<Page<ReadHomePostResponse>> postReadAll(@PageableDefault(
            size = 15, sort = "postId", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReadHomePostResponse>  response = homeService.readAllHomePost(pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
