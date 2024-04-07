package org.nurim.nurim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.nurim.nurim.config.auth.CustomAuthenticationManager;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.CreateMemberRequest;
import org.nurim.nurim.domain.dto.LoginRequest;
import org.nurim.nurim.domain.dto.LoginResponse;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.AuthService;
import org.nurim.nurim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    private final String email = "aaaa@gmail.com";
    private final String pw = "111111";

    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AuthService authService;

    @Mock
    private CustomAuthenticationManager customAuthenticationManager;


    @BeforeEach
    void beforeEach() {
        // 회원정보 생성
        CreateMemberRequest request = new CreateMemberRequest(email, pw, "test user");
        memberService.createMember(request);
    }

    @AfterEach
    void afterEach() {

        Member foundMember = memberService.findMemberByMemberEmail(email);
        memberService.deleteMember(foundMember.getMemberId());
    }

    @Test
    @DisplayName("시크릿키 인식 테스트")
    public void isSecretKeyExist() {
        assertThat(tokenProvider).isNotNull();
    }

    @Test
    @DisplayName("토큰 발급 테스트")
    public void testGenerate() {
        Map<String, Object> claimMap = Map.of("mid", "ABCDE");

        String jwtStr = tokenProvider.generateToken("mid");

        System.out.println("🐱‍🚀jwtStr🐱‍🚀 : " + jwtStr);
    }

    @Test
    @DisplayName("로그인 테스트")
    void loginTest()  throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest(email, pw);

        // when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is3xxRedirection())   // 리다이렉션 예상
//                .andExpect(redirectedUrl("/"))   // 리다이렉트 url
                .andReturn();

        // then
        String token = mvcResult.getResponse().getHeader("Authorization");
        assertNotNull(token, "토큰이 null 입니다.");
        Assertions.assertTrue(token.startsWith("Bearer ")); // 토큰이 "Bearer "로 시작하는지 확인
        token = token.replace("Bearer ", ""); // "Bearer " 부분 제거
        Assertions.assertTrue(tokenProvider.validateToken(token)); // 토큰 유효성 검사
    }

    @Test
    void authenticateMember_ValidRequest_ReturnsLoginResponseWithToken() {
        // Given
        String email = "aaaa@gmail.com";
        String password = "111111";

        LoginRequest request = new LoginRequest(email, password);

        // When
        LoginResponse response = authService.authenticateMember(request);

        // Then
        assertNotNull(response); // 반환된 객체가 null이 아닌지 확인
        assertNotNull(response.getToken()); // 토큰이 null이 아닌지 확인
    }


}
