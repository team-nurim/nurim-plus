package org.nurim.nurim.domain.entity.api;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nurim.nurim.domain.entity.Member;

import java.time.LocalDate;


//데이터베이스에 저장할 엔티티를 생성, dto와 비슷해도, 주로 데이터베이스 테이블과 1:1로 매핑되는 구조

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(length = 50, nullable = false)
    private String policyTitle;

    @Column(length = 500, nullable = false)
    private String policyDesc;

    @Column(nullable = false)
    private String policyRegion;

    @Column(nullable = false)
    private String policyCategory;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String policyApply;

    @Column(nullable = false)
    private String policyTarget;

    @Column(nullable = false)
    private String policyStandard;

    @Column(nullable = false)
    private String policySupport;

    @Column(nullable = false)
    private String policyJurisdiction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "memberId")
    private Member member;
}