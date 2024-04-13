package org.nurim.nurim.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.domain.entity.MemberRole;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMemberResponse {

    private Long memberId;
    private String memberEmail;
    private String memberPw;
    private String memberNickname;
    private int memberAge;
    private boolean gender;
    private String memberResidence;
    private boolean memberMarriage;
    private String memberIncome;
    private boolean type;
    private MemberRole memberRole;
    private String memberProfileImage;
}
