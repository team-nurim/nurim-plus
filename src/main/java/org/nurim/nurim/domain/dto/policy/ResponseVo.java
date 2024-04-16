package org.nurim.nurim.domain.dto.policy;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVo {
    String ucd;
    String message;

    //객체생성위해 생성자필요

}
// Vo는 dto와 다르게 응답출력용으로 만듬.(클라이언트가 볼수있게)