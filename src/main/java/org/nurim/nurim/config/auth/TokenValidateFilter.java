package org.nurim.nurim.config.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.exception.AccessTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class TokenValidateFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    // 토큰이 필요하지 않은 API URL
    List<String> list = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/members/user",
            "/api/v1/members/admin",
            "/api/v1/home/postList",
            "/api/v1/home/communityList",
            "/api/v1/home/popularCommunityList",
            "/api/v1/saveData"   // childcare 테이블 업데이트 엔드포인트
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if(!path.startsWith("/api/") || list.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("=============== TokenValidateFilter ===============");
        log.info("TokenProvider: " + tokenProvider);

        try {
            log.info("🤖 validateAccessToken 메소드 호출 전 ");
            validateAccessToken(request);

            Authentication authentication = tokenProvider.getAuthenticationFromToken(request.getHeader("Authorization"));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (AccessTokenException accessTokenException) {
            accessTokenException.sendResponseError(response);
        }
    }


    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {

        log.info("🤖 validateAccessToken 메소드 작동 시작 ");

        String headerStr = request.getHeader("Authorization");

        if(headerStr == null || headerStr.length() < 8) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        // Bearer 생략
        String tokenType = headerStr.substring(0, 6);
        String tokenStr = headerStr.substring(7);

        if(!tokenType.equalsIgnoreCase("Bearer")) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {
            log.info("🤖 TokenProvider의 validateToken 메소드 호출 전 ");
            return tokenProvider.validateToken(tokenStr);

        } catch (MalformedJwtException e) {
            log.error("MalformedJwtException--------------------------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);

        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException--------------------------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }

    }
}
