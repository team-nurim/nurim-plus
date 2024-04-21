package org.nurim.nurim.domain.dto.home;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nurim.nurim.domain.dto.reply.ReadReplyResponse;
import org.nurim.nurim.domain.entity.CommunityImage;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadHomeCommunityResponse {

    private Long communityId;

    private String title;   //게시글 제목

    private String content; //게시글 내용

    private String category; //게시글 카테고리

    private LocalDateTime registerDate;

    private LocalDateTime modifyDate;

    private Long counts;

    private Long recommend;

    private String memberNickname;

    private String memberProfileImage;

}
