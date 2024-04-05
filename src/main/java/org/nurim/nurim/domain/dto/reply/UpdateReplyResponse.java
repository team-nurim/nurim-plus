package org.nurim.nurim.domain.dto.reply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReplyResponse {

    private Long replyId;

    private String replyer;

    private String replyText;

    private LocalDateTime replyModifyDate;

}
