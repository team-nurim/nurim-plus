package org.nurim.nurim.domain.dto.community;

import lombok.*;
import org.nurim.nurim.domain.entity.CommunityImage;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class CreateCommunityResponse {

    private Long communityId;

    private String title;

    private String content;

    private LocalDateTime registerDate;

    private String category;
}
