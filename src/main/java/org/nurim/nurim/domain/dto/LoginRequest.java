package org.nurim.nurim.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class LoginRequest {

    private String memberEmail;
    private String memberPw;
}
