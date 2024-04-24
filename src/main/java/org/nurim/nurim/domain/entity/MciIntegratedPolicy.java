package org.nurim.nurim.domain.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
@Entity
@Getter
@Setter
@Table(name = "mciintegrated_policy")
public class MciIntegratedPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "offer_type")
    private String offerType;

    @Column(name = "payment_amount")
    private String paymentAmount;

    @Column(name = "support_target")
    private String supportTarget;

    @Column(name = "region")
    private String region;

    @Column(name = "business_overview")
    private String businessOverview;

    @Column(name = "support_details")
    private String supportDetails;

    @Column(name = "business_entity")
    private String businessEntity;

    @Column(name = "website")
    private String website;
}
