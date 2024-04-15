package org.nurim.nurim.Controller;

import com.amazonaws.services.s3.AmazonS3;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.FileDetail;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.dto.post.upload.UploadFileResponse;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.MemberImageService;
import org.nurim.nurim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class MemberImageController {

    private final MemberService memberService;
    private final MemberImageService memberImageService;
    private final FileUploadService fileUploadService;

//    @Value("${org.yeolmae.upload.path}")
//    private String uploadPath;

    @Autowired
    private AmazonS3 amazonS3Client;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 프로필 이미지 등록
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 업로드", description = "POST로 파일 등록")
    public ResponseEntity<UploadFileResponse> uploadProfile
    (@Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
     @RequestPart("files") MultipartFile files) {

        Member member = memberService.getMember();

        // 현재 로그인한 사용자가 해당 회원과 동일한지 확인
        if (!memberService.isCurrentUser(member.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403 Forbidden
        }

        // S3에 파일 업로드
        FileDetail fileDetail = fileUploadService.save(files);

        // DB에 이미지 경로 저장
        memberImageService.saveImage(fileDetail.getPath(), member.getMemberId());

        // 응답 생성
        UploadFileResponse response = UploadFileResponse.builder()
                .uuid(fileDetail.getId())
                .fileName(fileDetail.getName())
                .img(true) // 이미지인 경우 true로 설정
                .build();

        return ResponseEntity.ok(response);

    }

//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "프로필 이미지 업로드", description = "POST로 파일 등록")
//    public ResponseEntity<List<UploadFileResponse>> uploadProfile
//    (@Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
//     @RequestPart("files") MultipartFile[] files,
//     @RequestParam("memberId") Long memberId) {
//
//        // 현재 로그인한 사용자가 해당 회원과 동일한지 확인
//        if (!memberService.isCurrentUser(memberId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403 Forbidden
//        }
//
//        if (files != null) {
//
//            final List<UploadFileResponse> responses = new ArrayList<>();
//
//            for (MultipartFile multipartFile : files) {
//                String uuid = UUID.randomUUID().toString();
//                String originalName = multipartFile.getOriginalFilename();
//
//                Path savedPath = Paths.get(uploadPath, uuid + "_" + originalName);
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
//                        memberImageService.saveImage(savedPath.getFileName().toString(), member);
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
//
//            }
//
//            return ResponseEntity.ok(responses);
//        }
//        return ResponseEntity.badRequest().build();
//    }


//    // 프로필 이미지 조회
//    @GetMapping(value = "/view/{memberId}")
//    @Operation(summary = "프로필 이미지 파일 조회")
//    public ResponseEntity<Resource> getProfileByMemberId(@PathVariable @RequestParam("memberId") Long memberId) {
//
//        String fileName = memberImageService.getProfileImageFileName(memberId);
//
//        // 외부 URL 처리
//        if (fileName.startsWith("http")) {
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Location", fileName);
//            return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Redirect
//        }
//
//        Path imagePath = Paths.get(uploadPath + File.separator + fileName);
//
//        Resource resource = new FileSystemResource(imagePath);
//
//        // 파일 존재 확인 및 기본 이미지 처리
//        if (!resource.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // http 헤더 설정 : MIME 타입을 확인하고(proveContentType 메소드 사용), 해당 MIME 타입을 http 응답 헤더에 추가
//        HttpHeaders headers = new HttpHeaders();
//
//        try {
//            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
//
//        } catch (Exception e) {
//            // 파일 타입 확인 실패 시 내부 서버 오류 처리
//            return ResponseEntity.internalServerError().build();
//        }
//        return ResponseEntity.ok().headers(headers).body(resource);
//    }



    // 프로필 이미지 삭제
    @DeleteMapping(value = "/remove/{memberId}")
    @Operation(summary = "프로필 이미지 파일 삭제")
    public Map<String, Boolean> deleteProfile(@PathVariable Long memberId) {

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemoved = false;

        try {
            // memberId를 기반으로 프로필 이미지 삭제
            response = memberImageService.deleteImage(memberId);
            isRemoved = response.get("result");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        response.put("result", isRemoved);
        log.info(response);

        return response;

    }
//    @DeleteMapping(value = "/remove/{memberId}")
//    @Operation(summary = "프로필 이미지 파일 삭제")
//    public Map<String, Boolean> deleteProfile(@PathVariable Long memberId) {
//
//        Map<String, Boolean> response = new HashMap<>();
//        boolean isRemoved = false;
//
//        try {
//            // memberId를 기반으로 프로필 이미지 삭제
//            response = memberImageService.deleteImage(memberId);
//            isRemoved = response.get("result");
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//
//        response.put("result", isRemoved);
//        log.info(response);
//
//        return response;
//
//    }

}
