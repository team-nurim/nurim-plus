package org.nurim.nurim.Controller;

import com.amazonaws.services.s3.AmazonS3;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.dto.post.upload.UploadFileResponse;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.ExpertService;
import org.nurim.nurim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/experts")
public class ExpertController {

    private final MemberService memberService;
    private final ExpertService expertService;
    private final FileUploadService fileUploadService;

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${org.yeolmae.upload.path}")
    private String uploadPath;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 자격증 이미지 등록
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "자격증 이미지 업로드")
    public ResponseEntity<List<UploadFileResponse>> uploadExpertFile
    (@Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
     @RequestPart("files") MultipartFile[] files) {

        Member member = memberService.getMember();

        if (files != null) {

            final List<UploadFileResponse> responses = new ArrayList<>();

            for (MultipartFile multipartFile : files) {
                String fileName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
//                String uuid = UUID.randomUUID().toString();
//                String originalName = multipartFile.getOriginalFilename();

                // 이미지를 데이터베이스에 저장
                expertService.saveExpertFile(fileName, member.getMemberId());


                // 이미지 여부 초기화(default = false)
                boolean isImage = false;

                try {
                    // 실제 파일 저장
                    multipartFile.transferTo(savedPath);

                    // 저장된 파일이 MIME 유형인지 확인
                    if (Files.probeContentType(savedPath).startsWith("image")) {
                        isImage = true;

                        // memberId로 회원 정보 가져오기
                        Member member = memberService.getMemberById(memberId);

                        // 이미지를 데이터베이스에 저장
                        expertService.saveExpertFile(savedPath.getFileName().toString(), member);
                    }

                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                responses.add(UploadFileResponse.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(isImage)
                        .build());
            }
            return ResponseEntity.ok(responses);
        }
        return ResponseEntity.badRequest().build();
    }

    // 자격증 이미지 조회
    @GetMapping(value = "/view/{memberId}")
    @Operation(summary = "자격증 이미지 파일 조회")
    public ResponseEntity<Resource> getExpertfile(@PathVariable @RequestParam("memberId") Long memberId) {

        String fileName = expertService.getExpertImageFileName(memberId);
        Path imagePath = Paths.get(uploadPath + File.separator + fileName);

        Resource resource = new FileSystemResource(imagePath);

        // 파일 존재 확인 및 기본 이미지 처리
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // http 헤더 설정 : MIME 타입을 확인하고(proveContentType 메소드 사용), 해당 MIME 타입을 http 응답 헤더에 추가
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));

        } catch (Exception e) {
            // 파일 타입 확인 실패 시 내부 서버 오류 처리
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    // 자격증 이미지 삭제
    @DeleteMapping(value = "/remove/{memberId}")
    @Operation(summary = "자격증 이미지 파일 삭제")
    public Map<String, Boolean> deleteExpertFile(@PathVariable Long memberId) {

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemoved = false;

        try {
            // memberId를 기반으로 프로필 이미지 삭제
            response = expertService.deleteExpertFile(memberId);
            isRemoved = response.get("result");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        response.put("result", isRemoved);
        log.info(response);

        return response;

    }

}
