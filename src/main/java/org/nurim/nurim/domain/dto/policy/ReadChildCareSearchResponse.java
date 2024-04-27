package org.nurim.nurim.domain.dto.policy;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadChildCareSearchResponse {

    private Long id;
    private String sigunNm;
    private String bizNm;
    private String payment;
}
