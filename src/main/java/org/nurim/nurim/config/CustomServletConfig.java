package org.nurim.nurim.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CustomServletConfig implements WebMvcConfigurer {
    /** MVC 파일 경로 지정 (view 반환 시 설정) 필수 아님! */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("classpath:/static/**");
    }
}
