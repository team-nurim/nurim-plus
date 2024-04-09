package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 30, nullable = false, unique = true)
    private String memberEmail;

    @Column(length = 100,nullable = false)
    private String memberPw;

    @Column(length = 25, nullable = false)
    private String memberNickname;

    @Column(nullable = false)
    private int memberAge;

    @Column(nullable = false)
    private boolean gender;

    @Column(nullable = false)
    private String memberResidence;

    @Column(nullable = false)
    private boolean memberMarriage;

    @Column(nullable = false)
    private String memberIncome;

    @Column(nullable = false)
    private boolean type;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private MemberImage memberImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Community> community = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Policy> policies = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private Expert expert;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @Column(nullable = true) // expertFile 필드 추가
    private String expertFile;

    public String getMemberProfileImage() {
        return this.memberImage != null ? this.memberImage.getMemberProfileImage() : null;
    }

    // 회원 프로필 이미지 설정 메서드
    public void setMemberProfileImage(String memberProfileImage) {
        if (this.memberImage == null) {
            this.memberImage = new MemberImage();
            this.memberImage.setMember(this);
        }
        this.memberImage.setMemberProfileImage(memberProfileImage);
    }


    }



