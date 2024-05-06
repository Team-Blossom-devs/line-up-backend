package com.blossom.lineup.jwt.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtResponseConfigurer {

    private final JwtConfiguration jwtConfiguration;

    public void configureTokenResponse(HttpServletResponse response, String accessToken, String refreshToken) {

        response.setHeader(jwtConfiguration.getAccessHeader(), accessToken);

        ResponseCookie cookie = ResponseCookie.from(jwtConfiguration.getRefreshSetCookie(), refreshToken)
                .path("/")
                .httpOnly(true)
                .maxAge(TimeUnit.MINUTES.toMillis(jwtConfiguration.getCookieExpiration()))
                .secure(true)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }
}
