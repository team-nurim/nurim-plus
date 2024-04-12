package org.nurim.nurim.domain.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommunityResponse {

    private String title;

    private String content;

    private LocalDateTime modifyDate;
}
