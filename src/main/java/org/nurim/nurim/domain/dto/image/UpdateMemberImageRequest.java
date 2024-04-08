package org.nurim.nurim.domain.dto.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberImageRequest {

//    private String memberProfileImage;

    private MultipartFile memberProfileImage;

}
