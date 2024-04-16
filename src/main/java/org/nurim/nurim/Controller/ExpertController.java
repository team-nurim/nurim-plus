package org.nurim.nurim.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/experts")
public class ExpertController {

    private final MemberService memberService;
    private final ExpertService expertService;
    private final FileUploadService fileUploadService;

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

//    // 자격증 이미지 등록
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "자격증 이미지 업로드")
//    public ResponseEntity<List<UploadFileResponse>> uploadExpertFile
//    (@Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
//     @RequestPart("files") MultipartFile[] files) {
//
//        Member member = memberService.getMember();
//
//        if (files != null) {
//
//            final List<UploadFileResponse> responses = new ArrayList<>();
//
//            for (MultipartFile multipartFile : files) {
//                String fileName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
////                String uuid = UUID.randomUUID().toString();
////                String originalName = multipartFile.getOriginalFilename();
//
//                // 이미지를 데이터베이스에 저장
//                expertService.saveExpertFile(fileName, member.getMemberId());
//
//
//                // 이미지 여부 초기화(default = false)
//                boolean isImage = false;
//
//                try {
//                    // 실제 파일 저장
//                    multipartFile.transferTo(savedPath);
//
//                    // 저장된 파일이 MIME 유형인지 확인
//                    if (Files.probeContentType(savedPath).startsWith("image")) {
//                        isImage = true;
//
//                        // memberId로 회원 정보 가져오기
//                        Member member = memberService.getMemberById(memberId);
//
//                        // 이미지를 데이터베이스에 저장
//                        expertService.saveExpertFile(savedPath.getFileName().toString(), member);
//                    }
//
//                } catch (IOException e) {
//                    log.error(e.getMessage());
//                }
//
//                responses.add(UploadFileResponse.builder()
//                        .uuid(uuid)
//                        .fileName(originalName)
//                        .img(isImage)
//                        .build());
//            }
//            return ResponseEntity.ok(responses);
//        }
//        return ResponseEntity.badRequest().build();
//    }

    // 자격증 이미지 등록
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "자격증 이미지 업로드")
    public ResponseEntity<List<UploadFileResponse>> upload(
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
            @RequestPart("files") MultipartFile[] files,
            @RequestParam Long memberId) {

        if (files != null) {

            final List<UploadFileResponse> responses = new ArrayList<>();

            for (MultipartFile multipartFile : files) {
                String uuid = UUID.randomUUID().toString();
                String originalName = multipartFile.getOriginalFilename();

                // 이미지를 데이터베이스에 저장
                expertService.saveImage(memberId, uuid, originalName);

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


    // 자격증 이미지 조회
    @GetMapping(value = "/view/{uuid}")
    @Operation(summary = "자격증 이미지 파일 조회")
    public ResponseEntity<Resource> getExpertfile(@PathVariable String uuid) {
        try {
            // S3 클라이언트를 사용하여 해당 UUID로 이미지 파일을 가져옵니다.
            S3Object object = amazonS3Client.getObject(bucket, "images/" + uuid);

            // 가져온 객체가 null이면 해당 키에 해당하는 파일이 없는 것이므로 404를 반환합니다.
            if (object == null) {
                return ResponseEntity.notFound().build();
            }

            // 가져온 객체의 입력 스트림을 InputStreamResource로 변환합니다.
            InputStream inputStream = object.getObjectContent();
            InputStreamResource resource = new InputStreamResource(inputStream);

            // MIME 타입을 확인하여 HTTP 헤더에 추가합니다.
            String contentType = object.getObjectMetadata().getContentType();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (AmazonS3Exception e) {
            // Amazon S3에서 파일을 찾을 수 없는 경우 404를 반환합니다.
            if (e.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity.notFound().build();
            }
            // 다른 Amazon S3 예외 발생 시 500을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            // 그 외의 예외가 발생한 경우 500을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 자격증 이미지 삭제
    @DeleteMapping(value = "/remove/{uuid}")
    @Operation(summary = "자격증 이미지 파일 삭제")
    public Map<String, Boolean> deleteExpertFile(@PathVariable String uuid, Long memberId) {

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemovedFromDatabase = false;
        boolean isRemovedFromS3 = false;

        try {
            // 데이터베이스에서 파일 삭제 시도
            Map<String, Boolean> databaseResponse = expertService.deleteExpertFile(memberId);
            isRemovedFromDatabase = databaseResponse.get("result");

            // S3에서 파일 삭제 시도
            isRemovedFromS3 = fileUploadService.deleteFile(uuid);

            // 디버그 로그 추가
            log.info("Database removal status: " + isRemovedFromDatabase);
            log.info("S3 removal status: " + isRemovedFromS3);

        } catch (Exception e) {
            // 삭제 실패 시 에러 로그 출력
            log.error("Error occurred during file removal: " + e.getMessage());
        }

        // 두 작업 모두 성공했을 때만 결과를 true로 설정
        if (isRemovedFromDatabase && isRemovedFromS3) {
            response.put("result", true);
        } else {
            response.put("result", false);
        }

        log.info(response);
        return response;
    }

}
