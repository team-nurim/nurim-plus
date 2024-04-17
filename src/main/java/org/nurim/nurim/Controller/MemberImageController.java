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
import org.nurim.nurim.service.MemberImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class MemberImageController {

    private final MemberImageService memberImageService;
    private final FileUploadService fileUploadService;

    @Autowired
    private AmazonS3 amazonS3Client;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 프로필 이미지 등록
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 업로드", description = "POST로 파일 등록")
    public ResponseEntity<UploadFileResponse> uploadProfile
    (@Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
     @RequestPart("files") MultipartFile files,
     @RequestParam Long memberId) {

//        Member member = memberService.getMember();
        // memberId 인증 객체 여부 판단 로직 추가....

        if (files != null) {

            String uuid = UUID.randomUUID().toString();
            String originalName = files.getOriginalFilename();

            // DB에 이미지 uuid 저장
            memberImageService.saveImage(memberId, uuid, originalName);

            // S3에 파일 업로드
            fileUploadService.save(files);

            // 응답 생성
            UploadFileResponse response = UploadFileResponse.builder()
                    .uuid(uuid)
                    .fileName(originalName)
                    .img(true) // 이미지인 경우 true로 설정
                    .build();

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();

    }

    // 프로필 이미지 조회
    @GetMapping(value = "/view/{uuid}")
    @Operation(summary = "프로필 이미지 파일 조회")
    public ResponseEntity<Resource> getProfile(@PathVariable String uuid) {
        try {

            // S3 클라이언트를 사용하여 해당 파일명으로 이미지 파일을 가져옵니다.
            S3Object object = amazonS3Client.getObject(bucket, "images/" + uuid);

            // S3에 올라간 파일이 null이면 해당 회원의 프로필 이미지가 없는 것이므로 404를 반환합니다.
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

    // 프로필 이미지 삭제
    @DeleteMapping(value = "/remove/{uuid}")
    @Operation(summary = "프로필 이미지 파일 삭제")
    public Map<String, Boolean> deleteProfile(@PathVariable String uuid, Long memberId) {

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemovedFromDatabase = false;
        boolean isRemovedFromS3 = false;
        boolean isDefaultSet = false;

        try {
            // 데이터베이스에서 파일 삭제 시도 확인
            Map<String, Boolean> databaseResponse = memberImageService.deleteImage(memberId);
            isRemovedFromDatabase = databaseResponse.get("result");

            // S3에서 파일 삭제
            isRemovedFromS3 = fileUploadService.deleteFile(uuid);

            // 디버그 로그
            log.info("데이터베이스 삭제 상태(삭제 - true) : " + isRemovedFromDatabase);
            log.info("S3 삭제 상태(삭제 - true) : " + isRemovedFromS3);

            // 삭제가 모두 이뤄지면 default 이미지 uuid로 변경
            if (isRemovedFromDatabase && isRemovedFromS3) {
                isDefaultSet = memberImageService.setDefaultImage(memberId);

            }

            log.info("기본 이미지 설정 상태(설정 - true)" + isDefaultSet);


        } catch (Exception e) {
            // 삭제 실패 시 에러 로그 출력
            log.error("Error occurred during file removal: " + e.getMessage());
        }

        log.info(response);
        return response;

    }

}
