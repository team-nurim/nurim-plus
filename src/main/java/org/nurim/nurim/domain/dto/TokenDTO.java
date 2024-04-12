package org.nurim.nurim.domain.dto;

import lombok.*;

@Data
@Builder
public class TokenDTO {

    private final String grantType;
    private final String authorizationType;
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiresIn;
}
