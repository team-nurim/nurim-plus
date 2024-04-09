package org.nurim.nurim.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;
    private final MemberService memberService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // vue와의 연동으로 인한 CORS 정책 판단 조건
        if(HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // 인가 요청 확인
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        // JWT 여부 확인
        String accessToken = authorization.replaceAll("Bearer ", "");
        // token 유효성 검증
        if(accessToken != null && accessToken.length() > 10) {

            if(tokenProvider.validateToken(accessToken)) {
                ObjectMapper objectMapper = new ObjectMapper();

                // payload 내  Member 객체 추출
                String memberEmail = objectMapper.writeValueAsString(tokenProvider.getUsernameFromToken(accessToken));
                Member accessMember = memberService.findMemberByMemberEmail(memberEmail);

                // 추출한 정보 request에 적재
                request.setAttribute("member", accessMember);
                return true;
            }
        } else {
            throw new EntityNotFoundException("해당 회원 정보를 찾을 수 없습니다. Invalid Member. ");
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
