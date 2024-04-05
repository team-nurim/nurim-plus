package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", unique = true)
    private Member member;

    @Column(length = 500)
    private String memberProfileImage;

    public void update(String memberProfileImage){
        this.memberProfileImage = memberProfileImage;
    }

}
