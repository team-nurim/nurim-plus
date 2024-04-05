package org.nurim.nurim.config;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.config.auth.JwtAuthenticationFilter;
import org.nurim.nurim.config.auth.LoginFilter;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.repository.MemberRepository;
import org.nurim.nurim.service.MemberService;
import org.nurim.nurim.service.PrincipalDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private final UserDetailsService userDetailsService;

    @Autowired
    private final PrincipalDetailsService principalDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }


    // 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 권한별 허용 url 설정
        http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers("/", "/join", "/login").permitAll()   // 모든 사용자에게 접근 허용
                .requestMatchers("/api-document/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .anyRequest().authenticated()   // 나머지 페이지는 인증된 사용자에게만 접근 허용
        );

        // 로그인 설정
        http.formLogin((formLogin) -> formLogin
                .loginPage("/login").permitAll() // 로그인 페이지 url
                .usernameParameter("memberEmail")   // 이메일 입력 필드 지정
                .loginProcessingUrl("/login")   // 로그인 폼 제출 시 요청을 처리하는 엔드포인트
                .defaultSuccessUrl("/", true)   // 로그인 성공 시 리디렉션될 URL 지정
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



        // AuthenticationManger 설정
        // http로부터 인증설정 객체 가져오기
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(principalDetailsService)
                        .passwordEncoder(passwordEncoder());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.authenticationManager(authenticationManager);   // 반드시 필요

        // Login Filter
        LoginFilter loginFilter = new LoginFilter("/generateToken");
        loginFilter.setAuthenticationManager(authenticationManager);

        http
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)   // 로그인 전에 loginFilter 실행 설정
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);



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
        http.csrf(AbstractHttpConfigurer::disable);

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


    // AuthenticationProvider : 사용자 인증을 위해 DAO를 사용하는 인증 공급자
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer () {
        // 정적 리소스에 대한 보안 설정 무시
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
