package org.nurim.nurim.config.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.nurim.nurim.domain.dto.TokenDTO;
import org.nurim.nurim.service.PrincipalDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
@Getter
public class TokenProvider {
    /**
     * JWT 토큰 생성, 파싱 및 유효성 검증 클래스
     */

    public static final String BEARER_TYPE = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh";
    public static final String BEARER_PREFIX = "Bearer";

    @Autowired
    private PrincipalDetailsService principalDetailsService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Member 정보를 가지고 토큰 생성
    public String generateToken(Map<String, Object> valueMap, int days) {

        log.info("=============== TokenProvider : generateToken() 작동 ===============");
        // header
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        // deprecated 되어서 이젠 secretKey를 넣어줘야함
        Key secretKey = new SecretKeySpec(jwtSecret.getBytes(), SignatureAlgorithm.HS256.getJcaName());

        // payload
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);

        // 유효기간
        int time = (60 * 24) * days;   // 분단위

        String jwtStr = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        log.info("🎯jwtStr: " + jwtStr);

        return jwtStr;
    }

    // 토큰 검증
    public Map<String, Object> validateToken(String token) throws JwtException {
        log.info(" validateToken 메소드 동작 ");


        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        String memberEmailFromToken = claims.get("memberEmail").toString();

        return memberEmailFromToken;   /// 이메일 값 반환
    }

    // token 디코드 및 예외 발생 (토큰 만료, 시그니처 오류 시 Claims 객체가 안만들어짐)
    public Claims parseClaims(String token) {

        log.info("🤖 token : {}", token);
        if (token.startsWith("Bearer ")) {
            token = token.substring("Bearer ".length());
        }
        log.info("🤖 Baerer 제외 token : {}", token);

        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public Authentication getAuthenticationFromToken(String accessToken) {

        log.info("=============== TokenProvider의 getAuthenticationFromToken ===============");

        // 주어진 access token을 해석해서 포함된 claims 추출
        Claims claims = parseClaims(accessToken);


        if (claims.get("memberEmail") == null) {
            throw new UsernameNotFoundException("📢 유효한 토큰이 아닙니다.");
        }

        String memberEmail = claims.get("memberEmail").toString();
        UserDetails userDetails = principalDetailsService.loadUserByUsername(memberEmail);

        log.info("✅ claims.get(memberEmail).toString() = {}", claims.get("memberEmail").toString());
        log.info("✅ 회원 이메일 체크 = {}", memberEmail);
        log.info("✅ userDetails.getAuthorities : " + userDetails.getAuthorities());

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // Request Header에 access token 정보를 추출하는 메소드
    public String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.isEmpty(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
