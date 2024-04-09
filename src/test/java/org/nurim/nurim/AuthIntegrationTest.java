package org.nurim.nurim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.nurim.nurim.config.auth.AES128Config;
import org.nurim.nurim.config.auth.CustomAuthenticationManager;
import org.nurim.nurim.config.auth.PrincipalDetails;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.CreateMemberRequest;
import org.nurim.nurim.domain.dto.LoginRequest;
import org.nurim.nurim.domain.dto.LoginResponse;
import org.nurim.nurim.domain.dto.TokenDTO;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.AuthService;
import org.nurim.nurim.service.MemberService;
import org.nurim.nurim.service.PrincipalDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
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
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AuthService authService;

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;

    @Autowired
    private AES128Config aes128;

    @Autowired
    private PrincipalDetailsService principalDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


//    @BeforeEach
//    void beforeEach() {
//        // 회원정보 생성
//        CreateMemberRequest request = new CreateMemberRequest(email, pw, "test user");
//        memberService.createMember(request);
//    }
//
//    @AfterEach
//    void afterEach() {
//
//        Member foundMember = memberService.findMemberByMemberEmail(email);
//        memberService.deleteMember(foundMember.getMemberId());
//    }

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
    void loginTest() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest(email, pw);

        TokenDTO tokenDTO = TokenDTO.builder()
                .grantType("bearer")
                .authorizationType("Bearer")
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .accessTokenExpiresIn(3600L)
                .build();

        given(authService.authenticateMember(loginRequest)).willReturn(tokenDTO);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())   // 리다이렉션 예상
//                .andExpect(redirectedUrl("/"))   // 리다이렉트 url
                .andExpect((ResultMatcher) jsonPath("$.accessToken").value("access_token"));;

        // then
        assertTrue(tokenProvider.validateToken(tokenDTO.getAccessToken()));
    }


    @Test
    @DisplayName("AuthService 테스트")
    void authService_authenticateMember_ReturnsLoginResponseWithToken() {
        // Given
        LoginRequest request = new LoginRequest(email, pw);

        // When
        TokenDTO tokenDTO = authService.authenticateMember(request);

        // Then
        assertNotNull(tokenDTO); // 반환된 객체가 null이 아닌지 확인
        assertNotNull(tokenDTO.getAccessToken()); // 토큰이 null이 아닌지 확인
        assertNotNull(tokenDTO.getRefreshToken()); // 토큰이 null이 아닌지 확인

        // print result
        System.out.println("🎉access token : " + tokenDTO.getAccessToken());
        System.out.println("🎉refresh token : " + tokenDTO.getRefreshToken());
        System.out.println("🎉Expire : " + tokenDTO.getAccessTokenExpiresIn());
    }


    @Test
    @DisplayName("CustomAuthenticationManger 내 authenticate 메소드 테스트")
    public void testAuthenticate_Successful() {
        // given
        LoginRequest LoginRequest = new LoginRequest(email, pw);

        PrincipalDetailsService principalDetailsService = mock(PrincipalDetailsService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        CustomAuthenticationManager customAuthenticationManager = new CustomAuthenticationManager(principalDetailsService, passwordEncoder);

        UserDetails userDetails = mock(UserDetails.class);
        when(principalDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(passwordEncoder.matches(pw, userDetails.getPassword())).thenReturn(true);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, pw);

        // when
        try {
            customAuthenticationManager.authenticate(authentication);
        } catch (AuthenticationException e) {
            fail("Authentication에 실패하였습니다.: " + e.getMessage());
        }

        // then
        verify(principalDetailsService, times(1)).loadUserByUsername(email);
        verify(passwordEncoder, times(1)).matches(pw, userDetails.getPassword());

        // print result
        System.out.println("🎉userDetails.getUsername : " + userDetails.getUsername());
        System.out.println("🎉userDetails.getPassword : " + userDetails.getPassword());
        System.out.println("🎉Authentication : " + authentication);
    }

    @Test
    @DisplayName("AES128 암호화 테스트")
    public void test_aes128() throws Exception {

        String text = "This is a test";
        String enc = aes128.encryptAes(text);
        String dec = aes128.decryptAes(enc);

        // print result
        System.out.println("🎉enc : " + enc);
        System.out.println("🎉dec : " + dec);

        assertThat(dec).isEqualTo(text);
    }


}
