package org.nurim.nurim.domain.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadInquireResponse {

    private Long communityId;

    private String title;

    private String category;

    private String content;

    private Long viewCounts;

    private LocalDateTime registerDate;
}
