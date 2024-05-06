package com.blossom.lineup.jwt.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtResponseConfigurer {

    @Qualifier("accessHeader")
    private final String accessHeader;
    @Qualifier("refreshSetCookie")
    private final String refreshSetCookie;
    @Qualifier("cookieExpire")
    private final Long cookieExpire;

    public void configureTokenResponse(HttpServletResponse response, String accessToken, String refreshToken) {

        response.setHeader(accessHeader, accessToken);

        ResponseCookie cookie = ResponseCookie.from(refreshSetCookie, refreshToken)
                .path("/")
                .httpOnly(true)
                .maxAge(TimeUnit.MINUTES.toMillis(cookieExpire))
                .secure(true)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }
}
