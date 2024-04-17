package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 30, nullable = false, unique = true)
    private String memberEmail;

    @Column(length = 100, nullable = false)
    private String memberPw;

    @Column(length = 25, nullable = false)
    private String memberNickname;

    @Column(nullable = true)
    private int memberAge;

    @Column(nullable = true, columnDefinition = "boolean default false")
    private boolean gender;

    @Column(nullable = true)
    private String memberResidence;

    @Column(nullable = true, columnDefinition = "boolean default true")
    private boolean memberMarriage;

    @Column(nullable = true)
    private String memberIncome;

    @Column(nullable = true, columnDefinition = "boolean default false")
    private boolean type; // true: 전문가, false: 일반

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private MemberImage memberImage;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Community> community = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Policy> policies = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private Expert expert;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    public void update(String memberPw, String memberNickname, int memberAge, boolean gender, String memberResidence,
                       boolean memberMarriage, String memberIncome, boolean type) {

        this.memberPw = memberPw;
        this.memberNickname = memberNickname;
        this.memberAge = memberAge;
        this.gender = gender;
        this.memberResidence = memberResidence;
        this.memberMarriage = memberMarriage;
        this.memberIncome = memberIncome;
        this.type = type;

    }

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

    public String getExpertFile() {
        return this.expert != null ? this.expert.getExpertFile() : null;
    }

    public void setExpertFile(String expertFile) {
        if (this.expert == null) {
            this.expert = new Expert();
            this.expert.setMember(this);
        }
        this.expert.setExpertFile(expertFile);
    }

}
