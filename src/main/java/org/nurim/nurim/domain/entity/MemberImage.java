package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
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

    // url
    @Column(length = 500)
    private String memberProfileImage;

    // key 값
    @Column(length = 500)
    private String profileName;

    public void changeMember(Member member) {
        this.member = member;
    }

    public void update(String memberProfileImage){
        this.memberProfileImage = memberProfileImage;
    }
}
