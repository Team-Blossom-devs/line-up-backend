package com.blossom.lineup.handler;

import com.blossom.lineup.Member.CustomUserDetails;
import com.blossom.lineup.Member.ManagerRepository;
import com.blossom.lineup.jwt.config.JwtConfiguration;
import com.blossom.lineup.jwt.config.JwtResponseConfigurer;
import com.blossom.lineup.jwt.core.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ManagerRepository managerRepository;
    private final JwtResponseConfigurer jwtResponseConfigurer;
    private final JwtConfiguration jwtConfiguration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        String userName = extractUsername(authentication);
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        jwtResponseConfigurer.configureTokenResponse(response, accessToken, refreshToken);

        managerRepository.findByManagerName(userName)
                        .ifPresent(manager -> {
                            manager.updateRefreshToken(refreshToken);
                        });

        log.info("로그인에 성공하였습니다. managerName : {}", userName);
        log.info("로그인에 성공하였습니다. AccessToken : {}, refreshToken : {}", accessToken, refreshToken);
        log.info("발급된 AccessToken 만료 기간 : {} 분", TimeUnit.MILLISECONDS.toMinutes(jwtConfiguration.getAccessExpiration()));
    }

    private String extractUsername(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}