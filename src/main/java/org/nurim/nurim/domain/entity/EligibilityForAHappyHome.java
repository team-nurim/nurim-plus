package org.nurim.nurim.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EligibilityForAHappyHome {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String classification; // 분류 (예: 대학생 계층, 청년 계층 등)
    private String detailedClassification; // 상세 분류 (예: 대학생, 취업준비생 등)
    private String qualification; // 자격 조건
    private String incomeCriteria; // 소득 기준

    public EligibilityForAHappyHome() {
    }

    public EligibilityForAHappyHome(String classification, String detailedClassification, String qualification, String incomeCriteria) {
        this.classification = classification;
        this.detailedClassification = detailedClassification;
        this.qualification = qualification;
        this.incomeCriteria = incomeCriteria;
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDetailedClassification() {
        return detailedClassification;
    }

    public void setDetailedClassification(String detailedClassification) {
        this.detailedClassification = detailedClassification;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getIncomeCriteria() {
        return incomeCriteria;
    }

    public void setIncomeCriteria(String incomeCriteria) {
        this.incomeCriteria = incomeCriteria;
    }

    @Override
    public String toString() {
        return "HousingEligibility{" +
                "id=" + id +
                ", classification='" + classification + '\'' +
                ", detailedClassification='" + detailedClassification + '\'' +
                ", qualification='" + qualification + '\'' +
                ", incomeCriteria='" + incomeCriteria + '\'' +
                '}';
    }
}