package org.nurim.nurim.domain.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nurim.nurim.domain.dto.reply.ReadReplyResponse;
import org.nurim.nurim.domain.entity.CommunityImage;
import org.nurim.nurim.domain.entity.Reply;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadCommunityResponse {

    private Long communityId;

    private List<CommunityImage> link;

    private String title;   //게시글 제목

    private String content; //게시글 내용

    private String category; //게시글 카테고리

    private LocalDateTime registerDate;

    private LocalDateTime modifyDate;

    private Long counts;

    private Long recommend;

    private String memberNickname;

    List<ReadReplyResponse> replyResponseList;
}
