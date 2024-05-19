package com.blossom.lineup.oauth2.handler;

import com.blossom.lineup.Member.CustomerRepository;
import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.exceptions.BusinessException;
import com.blossom.lineup.jwt.config.JwtConfiguration;
import com.blossom.lineup.jwt.config.JwtResponseConfigurer;
import com.blossom.lineup.jwt.core.JwtTokenProvider;
import com.blossom.lineup.oauth2.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtResponseConfigurer jwtResponseConfigurer;
    @Value("${deploy.frontend-url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            CustomOAuth2User OAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            Customer customer = OAuth2User.getCustomer();

            //customer ROLE 이 GUEST 인 경우 추가 정보 입력 폼으로 리다이렉트
            if (customer.getRole().equals(Role.GUEST)) {
                log.info("customer id = {}, social_id = {} 추가 정보 입력 폼으로 redirect", customer.getId(),customer.getSocialId());

                Authentication generatedAuthentication = jwtTokenProvider.generateAuthentication(customer);
                String accessToken = jwtTokenProvider.generateAccessToken(generatedAuthentication);

                jwtResponseConfigurer.configureTokenResponse(response, accessToken, null);
                String redirectUrl = frontUrl + "/oauth" + "?token=" + accessToken;
                response.sendRedirect(redirectUrl);

            } else {
                loginSuccess(response, customer);
            }
        } catch (Exception e) {
            throw new BusinessException(Code.MEMBER_UNAUTHORIZED);
        }
    }

    private void loginSuccess(HttpServletResponse response, Customer customer) throws IOException {
        log.info("customer id = {}, nickname = {} OAuth 로그인", customer.getId(),customer.getUserName());
        Authentication generatedAuthentication = jwtTokenProvider.generateAuthentication(customer);
        String accessToken = jwtTokenProvider.generateAccessToken(generatedAuthentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        jwtResponseConfigurer.configureTokenResponse(response, accessToken, refreshToken);

        jwtTokenProvider.updateRefreshToken(customer.getUuid(), refreshToken, customer.getRole().getRole());
    }
}
