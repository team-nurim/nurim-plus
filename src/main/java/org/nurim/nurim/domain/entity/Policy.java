package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
