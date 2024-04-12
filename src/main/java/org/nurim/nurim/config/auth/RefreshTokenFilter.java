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

    /** "/refreshToken" 엔드포인트 요청 시 작동하는 필터
     *  클라이언트가 전송한 accessToken과 refreshToken을 검증하고, 필요에 따라 새로 생성하여 응답
     *  유효한 액세스 토큰을 사용하고 있으며, 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급할 수 있는지 확인 */

    private final String refreshPath;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

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

        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
        }

        Map<String, Object> refreshClaims = null;

        try {
            refreshClaims = checkRefreshToken(refreshToken);
            log.info(refreshClaims);

            // refresh token 유효기간이 얼마 남지 않은 경우
            Integer exp = (Integer) refreshClaims.get("exp");

            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
            Date current = new Date(System.currentTimeMillis());
            long gapTime = (expTime.getTime() - current.getTime());
            log.info("✅gapTime: " + gapTime);

            String memberEmail = (String)refreshClaims.get("memberEmail");

            // 무조건 access token 새로 생성
            String accessTokenValue = tokenProvider.generateToken(Map.of("memberEmail", memberEmail), 1);
            String refreshTokenValue = tokens.get("refreshToken");

            // refresh가 3일도 안남았다면
            if(gapTime < (1000 * 60 * 60 * 24 * 3)) {
                log.info("💌[만료까지 3일 이하] 새로운 refresh token 필요");
                refreshTokenValue = tokenProvider.generateToken(Map.of("memberEmail", memberEmail), 30);
            }

            log.info("✨accessToken: " + accessTokenValue);
            log.info("✨refreshToken: " + refreshTokenValue);

            // 토큰 전달
            sendTokens(accessTokenValue, refreshTokenValue, response);

        } catch(RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }

    }

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

    //
    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            tokenProvider.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("🚫Access Token Expired");
        } catch (Exception exception) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
    }

    // refresh token 검사
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

    // token 전송
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
