package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communityId")
    private Community community;

    @Column(length = 500)
    private String filePath;
}
