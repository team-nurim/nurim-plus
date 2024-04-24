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
public class ReadMciIntegratedSearchResponse {

    private Long id;
    private String offerType;
    private String paymentAmount;
    private String supportTarget;
    private String region;
    private String businessOverview;
    private String supportDetails;
    private String businessEntity;
    private String website;
}
