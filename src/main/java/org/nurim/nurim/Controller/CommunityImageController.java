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
import org.nurim.nurim.domain.entity.CommunityImage;
import org.nurim.nurim.service.CommunityImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "CommunityImage", description = "게시판 이미지 API")
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CommunityImageController {

    private final CommunityImageService communityImageService;
    private final FileUploadService fileUploadService;

    //게시판 이미지 등록

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping(value = "/communityImages/upload/{communityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = " 게시판 이미지 업록드", description = "Post방식 파일 등록")
    public ResponseEntity<UploadFileResponse> uploadCommunityImages(
            @PathVariable Long communityId,
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type ="string", format = "binary"))))
        @RequestPart("files") MultipartFile[] files){

        if(files != null && files.length > 0){
            List<CommunityImage> communityImageList = new ArrayList<>();

            for (MultipartFile multipartFile : files){
                if(!multipartFile.isEmpty()){
                    Map<String, String> s3Result = fileUploadService.saveUrlAndKey(multipartFile);
                            String url = s3Result.get("url");
                            String key = s3Result.get("key");

                            CommunityImage communityImage = CommunityImage.builder()
                                    .filePath(url)
                                    .fileKey(key)
                                    .build();

                    communityImageList.add(communityImage);
                }
            }
            communityImageService.saveImages(communityId, communityImageList);

            List<UploadFileResponse> responses = communityImageList.stream()
                    .map(communityImage -> UploadFileResponse.builder()
                            .uuid(communityImage.getFilePath())
                            .fileName(communityImage.getFileKey())
                            .img(true)
                            .build())
                    .toList();
            return ResponseEntity.ok((UploadFileResponse) responses);
        }
        return ResponseEntity.badRequest().build();
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping(value = "/communityImage/{communityImageId}")
    @Operation(summary = "이미지 파일 삭제", description = "DELETE 방식으로 파일 삭제")
    public ResponseEntity<String> deleteImages(@PathVariable Long communityImageId){
        boolean sucess = communityImageService.deleteCommunityImage(communityImageId);
        if (sucess) {
            return ResponseEntity.ok("이미지가 성공적으로 삭제 되었습니다.");
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제가 안됩니다.");
        }
    }
}