package org.nurim.nurim.domain.dto.post.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileResponse {

    private String uuid;
    private String fileName;
    private boolean img;


    public String getLink() {

        if(img) {
            // 이미지인 경우 썸네일 처리
            return "thumb_" + uuid + "_" + fileName;
        } else {
            return uuid + "_" + fileName;
        }

    }

}