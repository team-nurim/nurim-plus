package org.nurim.nurim.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberRequest {

    private String memberPw;
    private String memberNickname;
    private int memberAge;
    private boolean gender;
    private String memberResidence;
    private boolean married;
    private String memberIncome;
    private boolean type;
}
