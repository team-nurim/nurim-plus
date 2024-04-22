package org.nurim.nurim.Controller;

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
import org.nurim.nurim.domain.entity.PostImage;
import org.nurim.nurim.service.MemberService;
import org.nurim.nurim.service.PostImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "PostImage", description = "정책 추천 이미지 API")
@RestController
@RequiredArgsConstructor
@Log4j2
public class PostUpDownController {

    private final PostImageService postImageService;
    private final FileUploadService fileUploadService; // AWS S3 서비스 추가
    private final MemberService memberService;

    @PostMapping(value = "/api/v1/posts/post/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    // 첨부파일 삭제
    @DeleteMapping(value = "/api/v1/posts/post/delete/images/{postImageId}")
    @Operation(summary = "이미지 파일 삭제", description = "DELETE 방식으로 파일 조회")
    public ResponseEntity<String> deletePostImages(@PathVariable Long postImageId) {
        boolean success = postImageService.deletePostImages(postImageId);
        if (success) {
            return ResponseEntity.ok("Post images deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete post images.");
        }
    }

}
