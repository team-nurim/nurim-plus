package org.nurim.nurim.config.auth;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Log4j2
public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    /** access token을 이용하여 컨트롤러 호출 시 인증과 권한을 체크하는 기능
     *
     * AbstractAuthenticationProcessingFilter : 로그인 처리 담당
     * AuthenticationManager 설정 필수 -> SecurityConfig에서! */

    public LoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        /** 인증 요청 처리 메소드
         *  요청을 분석하고 인증 토큰을 생성하여 인증 매니저에 전달 */

        if(request.getMethod().equalsIgnoreCase("GET")) {
            log.info("GET Method Not Support");
            return null;
        }

        // 클라이언트에서 POST 요청 시 파싱된 JSON 문자열 처리 메소드
        Map<String, String> jsonData = parseRequestJSON(request);
        log.info(jsonData);

        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(jsonData.get("memberEmail"), jsonData.get("memberPw"));

        return getAuthenticationManager().authenticate(token);
    }


    // 요청으로부터 JSON 데이터를 파싱하여 map 형태로 반환
    private Map<String, String> parseRequestJSON(HttpServletRequest request) {

        // JSON 분석 후 id, pw를 Map 처리
        try (Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
