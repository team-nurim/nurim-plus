package org.nurim.nurim.domain.dto.post;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.nurim.nurim.domain.entity.PostImage;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ReadPostResponse {

    private Long postId;
    @NotEmpty
    private String postTitle;
    @NotEmpty
    private String postContent;
    @NotEmpty
    private String postWriter;
    @NotEmpty
    private String postCategory;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate postRegisterDate;
}
