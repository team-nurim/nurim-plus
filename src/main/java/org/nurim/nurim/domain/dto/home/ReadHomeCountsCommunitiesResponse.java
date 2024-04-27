package org.nurim.nurim.domain.dto.home;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadHomeCountsCommunitiesResponse {

    private Long communityId;
    private String title;
    private String content;
    private Long counts;
    private Long recommend;
    private LocalDateTime registerDate;
    private String memberNickname;
    private String memberProfileImage;
}
