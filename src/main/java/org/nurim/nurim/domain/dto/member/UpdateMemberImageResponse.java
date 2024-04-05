package org.nurim.nurim.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberImageResponse {

    private Long profileImageId;
    private String memberProfileImage;

}
