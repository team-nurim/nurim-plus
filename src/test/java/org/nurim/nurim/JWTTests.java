package org.nurim.nurim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    public void testGenerate() {
        Map<String, Object> claimMap = Map.of("aaaa@gmail.com", "111111");

        String jwtStr = tokenProvider.generateToken(claimMap, 1);

        System.out.println("🎈" + jwtStr);
    }

    @Test
    public void testLogin() throws Exception {
//        String memberEmail = "aaaa@gmail.com";
//        String memberPw = "111111";

        String loginRequestJson = objectMapper.writeValueAsString(new LoginRequest("test02", "string"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/generateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").exists())
                .andReturn();

        // returns the result as a string
        String resultString = result.getResponse().getContentAsString();

        // convert the string to JSON
        JSONObject responseJson = new JSONObject(resultString);

        System.out.println("🎈access token: " + responseJson.getString("accessToken"));
        System.out.println("🎈refresh token: " + responseJson.getString("refreshToken"));
    }

    @Test
    void testLogin2() throws Exception {
        LoginRequest loginRequest = new LoginRequest("aaaa@gmail.com", "111111");

        mockMvc.perform(MockMvcRequestBuilders.post("/generateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Authorization"))
                .andDo(result -> {
                    String authHeader = result.getResponse().getHeader("Authorization");
                    System.out.println("🎈access token: " + authHeader.split(" ")[1]);
                })
        ;
    }


    @Test
    public void testValidate() {

        String jwtStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhYWFhQGdtYWlsLmNvbSI6IjExMTExMSIsImlhdCI6MTcxMjgyOTUyMCwiZXhwIjoxNzEyOTE1OTIwfQ.1EzST9PjNEQZm6mCkt2fhDFQ2Qg7m2PmmSCJZGFFXaM";

        Map<String, Object> claim = tokenProvider.validateToken(jwtStr);

        System.out.println("✨" + claim);
    }

}
