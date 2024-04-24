package org.nurim.nurim.domain.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nurim.nurim.domain.entity.CommunityImage;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadAllCommunityResponse {

    private Long communityId;

    private List<String> communityImages;

    private String title;

    private String content;

    private String category;

    private LocalDateTime registerDate;

    private Long counts;

    private Long recommend;

    private String memberNickname;
}
