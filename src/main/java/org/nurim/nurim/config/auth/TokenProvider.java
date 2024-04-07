package org.nurim.nurim.config.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoder;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.nurim.nurim.domain.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
@Getter
public class TokenProvider {
    /** JWT 토큰 생성, 파싱 및 유효성 검증 클래스 */

    public static final String BEARER_TYPE = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh";
    public static final String BEARER_PREFIX = "Bearer";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration-millis}")
    private Long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-millis}")
    private Long refreshTokenExpirationMs;
    private Key key;   // JWT 서명을 생성하고 확인하는 비밀키



    // Member 정보를 가지고 토큰 생성
    public String generateToken(String username) {

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())   // 토큰 발급 시간
                .setExpiration(expireDate)   // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();   // JWT 문자열 생성
    }

    // secret 암호화하여 key 생성 (앱 시작 시 초기화)
    @PostConstruct
    public void init() {
        String base64EncodedSecretKey = encodeBase64SecretKey(this.jwtSecret);
        this.key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
    }

    // secret을 UTF8로 인코딩 -> base64 인코딩 후 반환
    public String encodeBase64SecretKey(String secret) {
        return Encoders.BASE64.encode(secret.getBytes(StandardCharsets.UTF_8));
    }

    // application.yml에서 secret 값을 가져와서 key로 반환
    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }



    public TokenDTO generateTokenDTO(PrincipalDetails principalDetails) {

        Date accessToeknExpiresIn = getTokenExpiration(accessTokenExpirationMs);
        Date refreshTokenExpiresIn = getTokenExpiration(refreshTokenExpirationMs);

        // claims : JWT에 저장되는 사용자 정보
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberType", principalDetails.getMemberTypeToString());

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(principalDetails.getUsername())
                .setExpiration(accessToeknExpiresIn)
                .setIssuedAt(Calendar.getInstance().getTime())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(principalDetails.getUsername())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key)
                .compact();

        return TokenDTO.builder()
                .grantType(BEARER_TYPE)
                .authorizationType(AUTHORIZATION_HEADER)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessToeknExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }



    // JWT 토큰을 디코딩하여 사용자 인증 정보 반환
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }
//    public Authentication getAuthenticationFromToken(String accessToken) {
//
//        // 주어진 access token을 해석해서 포함된 claims 추출
//        Claims claims = parseClaims(accessToken);
//
//        if(claims.get("memberType") == null) {
//            throw new UsernameNotFoundException("📢 Not Valid Aceess Token");
//        }
//
//        String memberType = claims.get("memberType").toString();
//        PrincipalDetails principalDetails = PrincipalDetails.of(claims.getSubject(), memberType);
//
//        log.info("#회원유형 체크 = {}", memberType);
//
//        return new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
//    }


    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
        } catch (MalformedJwtException e) {
            log.info("📢Invalid JWT token");
            log.trace("Invalid JWT token trace = {}", e);
        } catch (ExpiredJwtException e) {
            log.info("📢Expired JWT token");
            log.trace("Expired JWT token trace = {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("📢Unsupported JWT token");
            log.trace("Unsupported JWT token trace = {}", e);
        } catch (IllegalArgumentException e) {
            log.info("📢JWT claims String is empty");
            log.trace("JWT claims String is empty trace = {}", e);
        }
        return true;
    }



    // access token 만료일 반환
    private Date getTokenExpiration(Long accessTokenExpirationMs) {
        Date date = new Date();
        return new Date(date.getTime() + accessTokenExpirationMs);
    }
    
    // token 디코드 및 예외 발생 (토큰 만료, 시그니처 오류 시 Claims 객체가 안만들어짐)
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    public void accessTokenSetHeader(String accessToken, HttpServletResponse response) {
        String headerValue = BEARER_PREFIX + accessToken;
        response.setHeader(AUTHORIZATION_HEADER, headerValue);
    }

    public void refreshTokenSetHeader(String refreshToken, HttpServletResponse response) {
        response.setHeader("Refresh", refreshToken);
    }

    // Request Header에 access token 정보를 추출하는 메소드
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(!StringUtils.isEmpty(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Request Header에 refresh token 정보를 추출하는 메소드
    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(REFRESH_HEADER);
        if(!StringUtils.isEmpty(bearerToken)) {
            return bearerToken;
        }
        return null;
    }

}
