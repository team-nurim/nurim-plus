package org.nurim.nurim.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nurim.nurim.domain.entity.Member;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberImageResponse {

    private Long profileImageId;
//    private Member member;
    private String memberProfileImage;

}
