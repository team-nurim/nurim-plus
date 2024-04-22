package org.nurim.nurim.domain.dto.home;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadHomePostResponse {

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

    private String thumbImage;
}
