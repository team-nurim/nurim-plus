package org.nurim.nurim.domain.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "mcihousing_policy")
public class MciHousingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region")
    private String region;

    @Column(name = "business_overview")
    private String businessOverview;

    @Column(name = "support_details")
    private String supportDetails;

    @Column(name = "business_classification")
    private String businessClassification;

    @Column(name = "income_criteria")
    private String incomeCriteria;

    @Column(name = "asset_criteria")
    private String assetCriteria;

    @Column(name = "marriage_criteria")
    private String marriageCriteria;

    @Column(name = "business_entity")
    private String businessEntity;

    @Column(name = "website")
    private String website;


}
