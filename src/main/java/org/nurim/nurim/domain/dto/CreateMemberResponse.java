package org.nurim.nurim.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMemberResponse {

    private Long memberId;
    private String memberEmail;
    private String memberPw;
    private String memberNickname;
}
