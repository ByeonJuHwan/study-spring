package com.byeon.task.security;

import com.byeon.task.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final SecurityService securityService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .usernameParameter("userId")
                        .loginPage("/login") // 로그인 페이지 URL 설정
                        .loginProcessingUrl("/login") // 로그인 폼 제출 URL
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 리다이렉트할 URL
                        .failureUrl("/login?error=error!!") // 로그인 실패 시 리다이렉트할 URL
//                        .failureHandler((request, response, exception) -> log.error(exception.getMessage()))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 리다이렉트할 URL
                        .permitAll()
                )
                .userDetailsService(securityService);
        return http.build();
    }
}
