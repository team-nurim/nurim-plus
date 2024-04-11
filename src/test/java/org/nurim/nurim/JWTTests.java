package org.nurim.nurim;

import org.junit.jupiter.api.Test;
import org.nurim.nurim.config.auth.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class JWTTests {

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    public void testGenerate() {
        Map<String, Object> claimMap = Map.of("aaaa@gmail.com", "111111");

        String jwtStr = tokenProvider.generateToken(claimMap, 1);

        System.out.println("🎈" + jwtStr);
    }

    @Test
    public void testValidate() {

        String jwtStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhYWFhQGdtYWlsLmNvbSI6IjExMTExMSIsImlhdCI6MTcxMjgyOTUyMCwiZXhwIjoxNzEyOTE1OTIwfQ.1EzST9PjNEQZm6mCkt2fhDFQ2Qg7m2PmmSCJZGFFXaM";

        Map<String, Object> claim = tokenProvider.validateToken(jwtStr);

        System.out.println("✨" + claim);
    }

}
