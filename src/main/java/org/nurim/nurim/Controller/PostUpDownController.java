package org.nurim.nurim.Controller;

import com.amazonaws.services.s3.AmazonS3Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.dto.post.upload.UploadFileResponse;
import org.nurim.nurim.domain.entity.PostImage;
import org.nurim.nurim.service.PostImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class PostUpDownController {

    private final PostImageService postImageService;
    private final FileUploadService fileUploadService; // AWS S3 서비스 추가
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드", description = "POST로 파일 등록")
    public ResponseEntity<List<UploadFileResponse>> upload(
            @RequestParam Long postId,
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
            @RequestPart("files") MultipartFile[] files) {

        if (files != null && files.length > 0) {
            List<PostImage> postImages = new ArrayList<>();

            for (MultipartFile multipartFile : files) {
                if (!multipartFile.isEmpty()) {
                    Map<String, String> s3Result = fileUploadService.saveUrlAndKey(multipartFile);
                    String url = s3Result.get("url");
                    log.info(url);
                    String key = s3Result.get("key");
                    log.info(key);
                    PostImage postImage = PostImage.builder()
                            .image_detail(url)
                            .image_thumb(key)
                            .build();

                    postImages.add(postImage);
                }
            }
            postImageService.saveImages(postId, postImages);

            List<UploadFileResponse> responses = postImages.stream()
                    .map(postImage -> UploadFileResponse.builder()
                            .uuid(postImage.getImage_detail())
                            .fileName(postImage.getImage_thumb())
                            .img(true)
                            .build())
                    .toList();
            return ResponseEntity.ok(responses);
        }
        return ResponseEntity.badRequest().build();
    }

    // 첨부파일 조회
    // postId에 해당하는 모든 이미지의 URL을 조회하여 반환
//    @GetMapping("/post/{postId}")
//    @Operation(summary = "이미지 파일 조회", description = "GET 방식으로 파일 조회")
//    public ResponseEntity<List<String>> getImageUrlsByPostId(@PathVariable Long postId) {
//        List<String> imageUrls = postImageService.getImageUrlsByPostId(postId);
//        return ResponseEntity.ok(imageUrls);
//    }

//    @GetMapping(value = "/images/{uuid}")
//    @Operation(summary = "이미지 파일 조회", description = "GET 방식으로 파일 조회")
//    public ResponseEntity<Resource> getViewFile(@PathVariable String uuid) {
//        try {
//            // S3 클라이언트를 사용하여 해당 UUID로 이미지 파일을 가져옵니다.
//            S3Object object = amazonS3Client.getObject(bucket, "images/" + uuid);
//
//            // 가져온 객체가 null이면 해당 키에 해당하는 파일이 없는 것이므로 404를 반환합니다.
//            if (object == null) {
//                return ResponseEntity.notFound().build();
//            }
//
//            // 가져온 객체의 입력 스트림을 InputStreamResource로 변환합니다.
//            InputStream inputStream = object.getObjectContent();
//            InputStreamResource resource = new InputStreamResource(inputStream);
//
//            // MIME 타입을 확인하여 HTTP 헤더에 추가합니다.
//            String contentType = object.getObjectMetadata().getContentType();
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
//
//            return ResponseEntity.ok().headers(headers).body(resource);
//        } catch (AmazonS3Exception e) {
//            // Amazon S3에서 파일을 찾을 수 없는 경우 404를 반환합니다.
//            if (e.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
//                return ResponseEntity.notFound().build();
//            }
//            // 다른 Amazon S3 예외 발생 시 500을 반환합니다.
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        } catch (Exception e) {
//            // 그 외의 예외가 발생한 경우 500을 반환합니다.
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }


    // 첨부파일 삭제
    @DeleteMapping(value = "/images/{postId}")
    @Operation(summary = "이미지 파일 삭제", description = "DELETE 방식으로 파일 조회")
    public ResponseEntity<String> deletePostImages(@PathVariable Long postId) {
        boolean success = postImageService.deletePostImages(postId);
        if (success) {
            return ResponseEntity.ok("Post images deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete post images.");
        }
    }

}
