package org.nurim.nurim.domain.dto.community;

import lombok.*;
import org.nurim.nurim.domain.entity.CommunityImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class CreateCommunityRequest {

    private Long memberId;

    private String title; //게시글 제목

    private String content; // 게시글 내용

    private String category; //게시글 카테고리

}
