package com.blossom.lineup.jwt.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfiguration {

    private String secret;

    private Long accessExpiration;

    private Long refreshExpiration;

    private String accessHeader;

    private String refreshSetCookie;

    private Long cookieExpiration;

}
