package org.nurim.nurim.domain.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadCountsCommunitiesResponse {

    private Long communityId;

    private String title;

    private String category;

    private Long counts;

    private Long recommend;

    private String memberNickname;
}
