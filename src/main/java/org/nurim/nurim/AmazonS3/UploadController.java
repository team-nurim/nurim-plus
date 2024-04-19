package org.nurim.nurim.AmazonS3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/upload/s3", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UploadController {
    private final FileUploadService fileUploadService;

//    @PostMapping
//    public ResponseEntity<FileDetail> post(
//            @RequestPart("file") MultipartFile multipartFile) {
//        return ResponseEntity.ok(fileUploadService.save(multipartFile));
//    }

}