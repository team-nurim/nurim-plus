package org.nurim.nurim.domain.dto.post.upload;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UploadFileRequest {

    private List<MultipartFile> files;

}