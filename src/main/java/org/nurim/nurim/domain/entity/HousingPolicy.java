package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "housing_policy")
public class HousingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "save_time")
    private LocalDateTime saveTime; // 데이터 저장 시간

    @Column(name = "integrated_public_rental", columnDefinition = "LONGTEXT")
    private String integratedPublicRental;  // 통합공공임대

    @Column(name = "purchased_rental", columnDefinition = "LONGTEXT")
    private String purchasedRental;  // 매입임대

    @Column(name = "lumpsumlease_rental", columnDefinition = "LONGTEXT")
    private String lumpsumleaseRental;  // 전세임대

    @Column(name = "ahappyhouse", columnDefinition = "LONGTEXT")
    private String ahappyhouse;  // 행복주택

    @Column(name = "national_rental", columnDefinition = "LONGTEXT")
    private String nationalRental;  // 국민임대

    @Column(name = "permanent_public_rental", columnDefinition = "LONGTEXT")
    private String permanentPublicRental;  // 영구임대

    @Column(name = "alongtermlumpsumlease", columnDefinition = "LONGTEXT")
    private String alongtermlumpsumlease;  // 장기전세

    @Column(name = "publiclysupportedprivatelease", columnDefinition = "LONGTEXT")
    private String publiclysupportedprivatelease;  // 공공지원민간임대

    @Column(name = "publicsale", columnDefinition = "LONGTEXT")
    private String publicsale;  // 공공분양

    @Column(name = "newlywed_hope_town", columnDefinition = "LONGTEXT")
    private String newlywedHopeTown;  // 신혼희망타운

    @Column(name = "aspecialsupplyfornewlyweds", columnDefinition = "LONGTEXT")
    private String aspecialsupplyfornewlyweds;  // 신혼부부특별공급

    @Column(name = "thefirstspecialsupplyinoneslife", columnDefinition = "LONGTEXT")
    private String thefirstspecialsupplyinoneslife;  // 생애최초특별공급

    @PrePersist
    protected void onPrePersist() {
        this.saveTime = LocalDateTime.now(); // 엔티티 저장 직전에 호출되어 저장 시간 설정
    }
}
