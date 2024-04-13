package org.nurim.nurim.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMemberRequest {

    private String memberEmail;
    private String memberPw;
    private String memberNickname;
//    private int memberAge;
//    private boolean gender;
//    private String memberResidence;
//    private boolean memberMarriage;
//    private String memberIncome;
//    private boolean type;

}
