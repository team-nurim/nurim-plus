package org.nurim.nurim.config;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.config.auth.*;
import org.nurim.nurim.service.PrincipalDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private final PrincipalDetailsService principalDetailsService;

    @Autowired
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;


    // 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic(HttpBasicConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/login").permitAll());


        // 권한별 허용 url 설정
        http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers("/", "/join", "/login").permitAll()   // 모든 사용자에게 접근 허용
                .requestMatchers("/api-document/**", "/v3/api-docs/**", "/swagger-ui/**", "/api/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .anyRequest().authenticated()   // 나머지 페이지는 인증된 사용자에게만 접근 허용
        );

        // 로그인 설정
        http.formLogin((formLogin) -> formLogin
                        .loginPage("/login.html").permitAll() // 로그인 페이지 url
                        .usernameParameter("memberEmail")   // 이메일 입력 필드 지정
                        .loginProcessingUrl("/api/v1/auth/login")   // 로그인 폼 제출 시 요청을 처리하는 엔드포인트
//                .defaultSuccessUrl("/")   // 로그인 성공 시 리디렉션될 URL 지정
//                .failureUrl("/login.html?error=true")
//                .usernameParameter("memberEmail")
//                .passwordParameter("memberPw")
        );

        // remember-me 설정
        http.rememberMe((rememberMe) -> rememberMe
                .key("remember-me")
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(principalDetailsService)
                .tokenValiditySeconds(24*60*60)
        );

        // 인증 & 403 에러 처리
        http.exceptionHandling((exceptionConfig) ->
                exceptionConfig
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.getWriter().write("인가 에러: 해당 리소스에 접근할 권한이 없습니다.");
                        })
        );


        http.authenticationProvider(customAuthenticationProvider);
        http.authenticationManager(customAuthenticationManager);

        http.addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);


        // 로그아웃 설정
        http.logout((logout) -> logout
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/")
        );


        // security context 설정
        http.securityContext((securityContext) -> securityContext
                .securityContextRepository(new DelegatingSecurityContextRepository(
                        new RequestAttributeSecurityContextRepository(),
                        new HttpSessionSecurityContextRepository()
                ))
        );



        // csrf 비활성화
//        http.csrf(AbstractHttpConfigurer::disable);

        // 세션 설정
        // JWT 토큰 사용시 세션을 이용하는 방식으로 인증을 처리하지 않겠다는 설정 추가
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );


        return http.build();
    }


    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);

        tokenRepository.setCreateTableOnStartup(false);

        return tokenRepository;
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer () {
        // 정적 리소스에 대한 보안 설정 무시
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/swagger-ui/**")
                ;
    }


    // CORS 설정
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
