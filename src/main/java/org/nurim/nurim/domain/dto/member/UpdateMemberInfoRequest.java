package org.nurim.nurim.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberInfoRequest {

    private int memberAge;
    private boolean gender;
    private String memberResidence;
    private boolean memberMarriage;
    private String memberIncome;
    private boolean type;
    private String memberProfileImage;
    private String expertFile;

}
