package com.blossom.lineup.config;

import com.blossom.lineup.Member.CustomUserDetailsService;
import com.blossom.lineup.Member.ManagerRepository;
import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.filter.CustomJsonAuthenticationFilter;
import com.blossom.lineup.handler.LoginFailureHandler;
import com.blossom.lineup.handler.LoginSuccessHandler;
import com.blossom.lineup.jwt.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService customUserDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 기능을 비활성화
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 상태 없이 관리
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/","/css/**","/images/**", "/js/**",
                                "/index.html", "/favicon.ico").permitAll() // 일반 경로 허용
                        .requestMatchers("/api/sign-in/manager").permitAll() // 로그인 및 회원가입 경로 허용
                        .requestMatchers("/api/manager").hasRole(Role.MANAGER.getRole()) // MANAGER 역할을 가진 사용자만 접근 가능
                        .anyRequest().authenticated()) // 그 외 모든 요청은 인증 요구
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 HTTP 인증 비활성화
                .addFilterAfter(CustomJsonAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, CustomJsonAuthenticationFilter.class); // JwtAuthenticationFilter 추가

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(customUserDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public CustomJsonAuthenticationFilter CustomJsonAuthenticationFilter() {
        CustomJsonAuthenticationFilter jsonAuthenticationFilter
                = new CustomJsonAuthenticationFilter(objectMapper);

        jsonAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jsonAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        jsonAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler);

        return jsonAuthenticationFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // TODO: 운영 환경 배포시 strict 하게 설정
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addExposedHeader("*");
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        corsConfiguration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5672",
                "http://localhost:15672",
                "http://localhost:61613",
                "http://localhost:8080"
        ));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

}
