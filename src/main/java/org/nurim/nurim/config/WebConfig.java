package org.nurim.nurim.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    /** CORS 설정 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/")
                .allowedOrigins("http://localhost:8080", "http://localhost:3306")
                .allowedHeaders("*")
                .allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE")
                .allowCredentials(true)   // 쿠키 포함 인증정보 허용여부 지정
                .maxAge(3600);
    }

}
