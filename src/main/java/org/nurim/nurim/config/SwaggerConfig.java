package org.nurim.nurim.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    // 토큰이 필요하지 않은 API URL
    List<String> list = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/members/user",
            "/api/v1/members/admin",
            "/api/v1/home/postList",
            "/api/v1/home/communityList",
            "/api/v1/home/popularCommunityList"
    );

    @Bean
    public OpenAPI openAPI(){
        SecurityScheme securityScheme = getSecurityScheme();
        SecurityRequirement securityRequirement = getSecurityRequirement();

        return new OpenAPI()
                // 보안 스키마 추가 (JWT 토큰 인증)
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                // 보안 요구사항 지정
                .security(List.of(securityRequirement))
                // api 정보 지정
                .info(apiInfo());
    }

    private Info apiInfo(){
        return new Info()
                .title("누림 API Test")
                .description("누림 팀의 누림 어플리케이션 API 입니다.")
                .version("1.0.0");
    }


    /** 보안 관련 헤더 추가를 위한 설정 */
    private SecurityScheme getSecurityScheme() {
        return new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);
    }

    // 각 API에 대한 보안 요구 지정
    private SecurityRequirement getSecurityRequirement() {
        SecurityRequirement securityRequirement = new SecurityRequirement();

        // 보안이 필요한 API에 대한 SecurityRequirement 생성
        securityRequirement.addList("bearerAuth");

        // 보안이 필요하지 않은 API 제외처리
        for(String endpoint: list) {
            securityRequirement.remove(endpoint);
        }

        return securityRequirement;
    }


}
