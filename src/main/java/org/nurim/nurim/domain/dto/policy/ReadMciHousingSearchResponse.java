package org.nurim.nurim.domain.dto.policy;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadMciHousingSearchResponse {

    private Long id;
    private String region;
    private String businessOverview;
    private String supportDetails;
    private String businessClassification;
    private String incomeCriteria;
    private String assetCriteria;
    private String marriageCriteria;
    private String businessEntity;
    private String website;
}
