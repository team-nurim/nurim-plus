package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Setter
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Expert> experts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @Column(nullable = false)
    private String memberGrade;
}
