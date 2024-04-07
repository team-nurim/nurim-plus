package org.nurim.nurim.config.auth;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Autowired
    private final PasswordEncoder passwordEncoder;



    // AuthenticationProvider : 사용자 인증을 위해 DAO를 사용하는 인증 공급자
    public CustomAuthenticationProvider customAuthenticationProvider() throws Exception {

        CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider(passwordEncoder);

        customAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        return customAuthenticationProvider;
    }

}
