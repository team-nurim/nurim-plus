package org.nurim.nurim.domain.dto.post;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ImagePostDTO {

    @NotEmpty
    private String image_detail;
    @NotEmpty
    private String image_thumb;
}
