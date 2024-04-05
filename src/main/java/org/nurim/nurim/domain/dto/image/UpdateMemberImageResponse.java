package org.nurim.nurim.domain.dto.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberImageResponse {

    private Long memberId;
    private String memberProfileImage;

}
