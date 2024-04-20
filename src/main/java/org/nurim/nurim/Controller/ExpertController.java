package org.nurim.nurim.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.dto.post.upload.UploadFileResponse;
import org.nurim.nurim.service.ExpertService;
import org.nurim.nurim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Tag(name = "Expert", description = "자격증 이미지 API")
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/experts")
public class ExpertController {

    private final ExpertService expertService;
    private final FileUploadService fileUploadService;

    // 자격증 이미지 등록
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "자격증 이미지 업로드", description = "POST로 파일 등록")
    public ResponseEntity<UploadFileResponse> uploadExpertFile
    (@Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
     @RequestPart("files") MultipartFile files,
     @RequestParam Long memberId) {
        if (files != null) {

            // S3 에서의 이미지 저장 및 url, key값 반환
            Map<String, String> s3Result = fileUploadService.saveUrlAndKey(files);
            String url = s3Result.get("url");
            log.info(url);
            String key = s3Result.get("key");
            log.info(key);

            // DB에 이미지 url 저장
            expertService.saveImage(memberId, url, key);

            UploadFileResponse response = UploadFileResponse.builder()
                    .uuid(key)
                    .fileName(files.getOriginalFilename())
                    .img(true)
                    .build();

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }

    // 자격증 이미지 삭제
    @PutMapping(value = "/remove/{memberId}")
    @Operation(summary = "자격증 이미지 파일 삭제")
    public Map<String, Boolean> deleteExpertFile(@PathVariable Long memberId) {

        Map<String, Boolean> response = new HashMap<>();
        boolean isDeletedAndSetDefault = false;

        try {
            // 이미지 삭제 및 기본값으로 변경
            isDeletedAndSetDefault = expertService.deleteAndSetDefault(memberId);
            log.info("자격증 이미지 삭제 및 기본값으로 변경 상태: " + isDeletedAndSetDefault);
        } catch (Exception e) {
            // 처리 중 에러가 발생한 경우 로그 출력
            log.error("자격증 이미지 삭제 및 기본값으로 변경 중 오류 발생: " + e.getMessage());
        }

        response.put("success", isDeletedAndSetDefault);
        return response;
    }

}
