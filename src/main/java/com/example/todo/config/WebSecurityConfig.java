package com.example.todo.config;

import com.example.todo.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@AllArgsConstructor
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http 시큐리티 빌더
        http
                .csrf((csrfConfig) -> csrfConfig.disable()) // csrf 사용하지 않으므로 disable
                .httpBasic((httpBasicConfig) -> httpBasicConfig.disable()) // token을 사용하므로 basic 인증 disable
                .sessionManagement((sessionManagementConfig)
                        -> sessionManagementConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session 기반이 아님을 선언
                .authorizeRequests((authorizeRequests)
                        -> authorizeRequests.requestMatchers(new AntPathRequestMatcher("/"), new AntPathRequestMatcher("/auth/**")).permitAll() // "/"와 "/auth/**" 경로는 인증 안 해도 됨
                        .anyRequest().authenticated()) // "/"와 "/auth/**" 경로 외에는 인증 필요
        ;

        // filter 등록
        // 매 요청마다 CorsFilter 실행한 후에
        // jwtAuthenticationFilter 실행
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
