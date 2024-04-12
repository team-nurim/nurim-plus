package org.nurim.nurim.domain.dto.reply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReplyResponse {

    private Long replyId;

    private Long communityId;

    private String replyer;

    private String replyText;

    private LocalDateTime replyRegisterDate;
}
