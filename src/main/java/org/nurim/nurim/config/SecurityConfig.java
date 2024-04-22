package org.nurim.nurim.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.config.auth.*;
import org.nurim.nurim.service.PrincipalDetailsService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


import javax.sql.DataSource;
import java.util.Collections;

import static org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL;


@Configuration
@EnableWebSecurity  // SpringSecurity FilterChain이 자동으로 포함
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)// secured, PreAuthorize/postAuthorize 어노테이션 활성화 //특정 경로에 접근할 수 있는 권한
@RequiredArgsConstructor
public class SecurityConfig {

    private final DataSource dataSource;
    private final TokenProvider tokenProvider;

    @Autowired
    private PrincipalDetailsService principalDetailsService;



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {

        // Authentication Manager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(principalDetailsService)
                .passwordEncoder(passwordEncoder());

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);   // LoginFilter

        // LoginFilter
        LoginFilter loginFilter = new LoginFilter("/generateToken");
        loginFilter.setAuthenticationManager(authenticationManager);

        // LoginSuccessHandler 세팅
        LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler(tokenProvider);
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);

        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);   // 로그인 필터 위치 조정
        http.addFilterBefore(tokenValidateFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken", tokenProvider), TokenValidateFilter.class);

        // 권한에 따른 허용하는 url
//        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//                .requestMatchers("/login","/swagger-ui/**","/swagger-resources/**","/v3/api-docs/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
//                .requestMatchers( "/error").hasRole("USER") // 권한설정 필요
//                .anyRequest().permitAll());   //나머지 페이지들은 모두 권한 허용

//        // 권한에 따른 허용하는 url
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers("/admin/**").hasRole("ADMIN")   //권한 있어야 함
                .requestMatchers("/", "/login", "/join").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/error").permitAll()
                .requestMatchers("/api/v1/**").permitAll()
                .anyRequest().authenticated());   //나머지 페이지들은 모두 권한 허용

        // 자동로그인 설정
        http.rememberMe((rememberMe) -> rememberMe
                .key("remember-me")   // 인증받은 사용자 정보로 토큰 생성에 필요한 값
                .rememberMeParameter("remember-me")   // html에서의 name 값
                .tokenValiditySeconds(7*24*60*60)   // remember-me 토큰 유효시간 : 7일
                .rememberMeServices(rememberMeServices(persistentTokenRepository()))
                .userDetailsService(new PrincipalDetailsService()));

        // logout 설정
        http.logout((logout) -> logout
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/"));

        // csrf 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 세션 비활성화
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // cors
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:8081"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L); //1시간
                        return config;
                    }
                }));

        // context 설정
        http.securityContext((securityContext) -> securityContext
                .securityContextRepository(new DelegatingSecurityContextRepository(
                        new RequestAttributeSecurityContextRepository(),
                        new HttpSessionSecurityContextRepository()
                )));

        SecurityContextHolder.setStrategyName(MODE_INHERITABLETHREADLOCAL);

        return http.build();
    }


    private TokenValidateFilter tokenValidateFilter(TokenProvider tokenProvider) {
        return new TokenValidateFilter(tokenProvider);
    }


    // remember-me 토큰을 DB에 저장하고 검색하는 기능 (JdbcTokenRepositoryImpl로 구현)
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {

        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);

        tokenRepository.setCreateTableOnStartup(false);

        return tokenRepository;
    }

    @Bean
    public PersistentTokenBasedRememberMeServices rememberMeServices(PersistentTokenRepository tokenRepository){
        PersistentTokenBasedRememberMeServices rememberMeServices
                = new PersistentTokenBasedRememberMeServices("rememberMeKey", new PrincipalDetailsService(), tokenRepository);
        rememberMeServices.setParameter("remember-me");
        rememberMeServices.setAlwaysRemember(true);

        return rememberMeServices;
    }


    // UserDetailsService 및 PasswordEncoder를 사용하여 사용자 아이디와 암호를 인증하는 AuthenticationProvider 구현
//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
//
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(principalDetailsService);
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//
//        return daoAuthenticationProvider;
//    }

    // css 나 js 파일 등의 정적 파일은 시큐리티 적용을 받을 필요 없이 무시하도록 함.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }



}