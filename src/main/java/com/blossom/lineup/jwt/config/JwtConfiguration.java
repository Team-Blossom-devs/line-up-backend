package com.blossom.lineup.jwt.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.concurrent.TimeUnit;

@Configuration
public class JwtConfiguration {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpire;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpire;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.set_cookie}")
    private String refreshSetCookie;

    @Value("${jwt.cookie.expiration}")
    private Long cookieExpire;

    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Bean
    @Qualifier("accessTokenExpire")
    public Long AccessTokenExpire() {
        return TimeUnit.MINUTES.toMillis(accessTokenExpire);
    }

    @Bean
    @Qualifier("refreshTokenExpire")
    public Long refreshTokenExpire() {
        return TimeUnit.MINUTES.toMillis(refreshTokenExpire);
    }

    @Bean
    @Qualifier("accessHeader")
    public String AccessHeader() {
        return accessHeader;
    }

    @Bean
    @Qualifier("refreshSetCookie")
    public String refreshSetCookie() {
        return refreshSetCookie;
    }

    @Bean
    @Qualifier("cookieExpire")
    public Long cookieExpire() {
        return TimeUnit.MINUTES.toMillis(cookieExpire);
    }
}
