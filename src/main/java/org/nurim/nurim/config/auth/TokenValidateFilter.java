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
            "/api/v1/members/admin"
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
            validateAccessToken(request);
            filterChain.doFilter(request, response);

        } catch (AccessTokenException accessTokenException) {
            accessTokenException.sendResponseError(response);
        }
    }


    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {

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
            Map<String, Object> values = tokenProvider.validateToken(tokenStr);
            return values;

        } catch (MalformedJwtException e) {
            log.error("MalformedJwtException--------------------------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);

        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException--------------------------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }

    }
}
