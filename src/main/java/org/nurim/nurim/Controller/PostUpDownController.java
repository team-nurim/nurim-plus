package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.dto.post.upload.UploadFileResponse;
import org.nurim.nurim.service.PostImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class PostUpDownController {

    private PostImageService postImageService;
    private FileUploadService fileUploadService; // AWS S3 서비스 추가


    @Value("${cloud.aws.s3.bucket}")
    private String uploadPath;

    public PostUpDownController(PostImageService postImageService, FileUploadService fileUploadService) {
        this.postImageService = postImageService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드", description = "POST로 파일 등록")
    public ResponseEntity<List<UploadFileResponse>> upload(
            @RequestParam Long postId,
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
            @RequestPart("files") MultipartFile[] files) {

        if (files != null) {

            final List<UploadFileResponse> responses = new ArrayList<>();

            for (MultipartFile multipartFile : files) {
                String uuid = UUID.randomUUID().toString();
                String originalName = multipartFile.getOriginalFilename();

                // 이미지를 데이터베이스에 저장
                postImageService.saveImage(postId, originalName, uuid);

                // 이미지를 아마존 s3에 저장
                fileUploadService.save(multipartFile);

                responses.add(UploadFileResponse.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(true)
                        .build());
            }

            return ResponseEntity.ok(responses);
        }
        return ResponseEntity.badRequest().build();
    }

    // 첨부파일 조회
    @GetMapping(value = "/view/{fileName}")
    @Operation(summary = "이미지 파일 조회", description = "GET 방식으로 파일 조회")
    public ResponseEntity<Resource> getViewFile(@PathVariable String fileName) {

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // http 헤더 설정 : MIME 타입을 확인하고(proveContentType 메소드 사용), 해당 MIME 타입을 http 응답 헤더에 추가
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    // 첨부파일 삭제
    @DeleteMapping(value = "/remove/{fileName}")
    @Operation(summary = "이미지 파일 삭제", description = "DELETE 방식으로 파일 조회")
    public Map<String, Boolean> removeFile(@PathVariable String fileName) {

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemoved = false;

        try {
            // 이미지 파일 삭제 후 데이터베이스에서도 삭제
            response = postImageService.deleteImage(fileName);
            isRemoved = response.get("result");

            // 파일 삭제 시도
            if (resource.exists()) {
                isRemoved = resource.getFile().delete();

                // 썸네일 존재 시 삭제
                String contentType = Files.probeContentType(resource.getFile().toPath());
                if (contentType.startsWith("image")) {
                    File thumb = new File(uploadPath + File.separator + "thumb_" + fileName);
                    thumb.delete();
                }
            }

        } catch (Exception e) {
            // 삭제 실패 시 에러 로그 출력
            log.error(e.getMessage());
        }

        // 결과를 response 맵에 추가
        response.put("result", isRemoved);
        log.info(response);

        return response;
    }
}
