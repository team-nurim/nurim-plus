package org.nurim.nurim.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.nurim.nurim.domain.dto.TokenDTO;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    /** 사용자 인증(로그인) 및 JWT 발급 */

    @Autowired
    private final MemberService memberService;

    @Autowired
    private final TokenProvider tokenProvider;

    @Autowired
    private final AES128Config aes128Config;


    //
    public JwtAuthenticationFilter(MemberService memberService, TokenProvider tokenProvider, AES128Config aes128Config, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.memberService = memberService;
        this.tokenProvider = tokenProvider;
        this.aes128Config = aes128Config;
    }


    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response) {

        try {
            String jwt = getJwtFromRequest(request);

            if(jwt != null && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 예외처리
            logger.error("JWT 인증 오류 발생");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 인증 오류 발생");
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    @SneakyThrows
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        TokenDTO tokenDTO = tokenProvider.generateTokenDTO(principalDetails);

        String accessToken = tokenDTO.getAccessToken();
        String refreshToken = tokenDTO.getRefreshToken();

        String encryptedRefreshToken = aes128Config.encryptAes(refreshToken);

        tokenProvider.accessTokenSetHeader(accessToken, response);
        tokenProvider.refreshTokenSetHeader(encryptedRefreshToken, response);


        Member member = memberService.findMemberByMemberEmail(principalDetails.getUsername());

        // JSON 형식으로 멤버 정보 전달
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), member);
    }

}
