package org.nurim.nurim.config.auth;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.exception.RefreshTokenException;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final TokenProvider tokenProvider;

    // "/refreshToken" 엔드포인트 요청 시 작동하는 필터
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /** "/refresh" 요청에 대한 access token과 refresh token 확인하고 새 토큰 발행  */
        String path = request.getRequestURI();

        // 요청이 "/refreshToken" 엔드포인트가 아니면 필터 스킵
        if(!path.equals(refreshPath)) {
            log.info("skip refresh token filter");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("✨refresh token filter run");

        // 전송된 JSON에서 access token과 refresh token 추출
        Map<String, String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("accessToken: " + accessToken);
        log.info("refreshToken: " + refreshToken);

        // 액세스 토큰 검증
        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
        }

        Map<String, Object> refreshClaims = null;

        // 리프레시 토큰 검증
        try {
            refreshClaims = checkRefreshToken(refreshToken);
            log.info(refreshClaims);

            // refresh token 유효기간 확인
            Integer exp = (Integer) refreshClaims.get("exp");

            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
            Date current = new Date(System.currentTimeMillis());
            long gapTime = (expTime.getTime() - current.getTime());
            log.info("✅gapTime: " + gapTime);

            String memberEmail = (String)refreshClaims.get("memberEmail");

            // 무조건 access token 새로 생성
            String accessTokenValue = tokenProvider.generateToken(Map.of("memberEmail", memberEmail), 1);
            String refreshTokenValue = tokens.get("refreshToken");

            // refresh token이 3일 이하로 남았을 경우 새로 생성
            if(gapTime < (1000 * 60 * 60 * 24 * 3)) {
                log.info("💌[만료까지 3일 이하] 새로운 refresh token 필요");
                refreshTokenValue = tokenProvider.generateToken(Map.of("memberEmail", memberEmail), 30);
            }

            log.info("✨accessToken: " + accessTokenValue);
            log.info("✨refreshToken: " + refreshTokenValue);

            // 새로 생성된 토큰 응답
            sendTokens(accessTokenValue, refreshTokenValue, response);

        } catch(RefreshTokenException refreshTokenException) {
            // 리프레시 토큰 검증 실패 시 오류 응답
            refreshTokenException.sendResponseError(response);
            return;
        }

    }

    // 요청된 JSON 파싱하여 맵으로 변환
    public Map<String, String> parseRequestJSON(HttpServletRequest request) {

        // JSON에서 id, pw 값을 Map 처리
        try(Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);

        } catch(Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    // 액세스 토큰 검증
    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            tokenProvider.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("🚫Access Token Expired");
        } catch (Exception exception) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
    }

    // refresh token 검증
    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {

        try {
            Map<String, Object> values = tokenProvider.validateToken(refreshToken);
            return values;
        } catch (ExpiredJwtException expiredJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        } catch (MalformedJwtException malformedJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.BAD_REFRESH);
        } catch (Exception e) {
            new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
        return null;
    }

    // 응답으로 토큰 전송
    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue));

        try {
            response.getWriter().println(jsonStr);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

    }
}
