package org.nurim.nurim.domain.dto.reply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadReplyResponse {

    private Long replyId;

    private Long communityId;

    private String replyText;

    private String memberNickname;

    private String memberEmail;

    private LocalDateTime replyRegisterDate;

    private LocalDateTime replyModifyDate;
}
