package org.nurim.nurim.domain.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadInquireRespose {

    private Long communityId;

    private String title;

    private Long counts;

    private LocalDateTime registerDate;
}
