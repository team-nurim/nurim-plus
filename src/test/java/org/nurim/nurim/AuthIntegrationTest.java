package org.nurim.nurim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.CreateMemberRequest;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isNotNull;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        // 회원정보 생성
        CreateMemberRequest request = new CreateMemberRequest(
                email, pw, "test user");
    }

//    @AfterEach
//    void afterEach() {
//        memberService.deleteMember()
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
    void loginTest()  throws Exception {


    }


}
