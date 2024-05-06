package com.blossom.lineup.jwt.core;

import com.blossom.lineup.Member.CustomUserDetails;
import com.blossom.lineup.Member.CustomerRepository;
import com.blossom.lineup.Member.ManagerRepository;
import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.exceptions.BusinessException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final SecretKey key;
    @Qualifier("accessTokenExpire")
    private final Long accessTokenExpire;
    @Qualifier("refreshTokenExpire")
    private final Long refreshTokenExpire;
    @Qualifier("accessHeader")
    private final String accessHeader;
    @Qualifier("refreshSetCookie")
    private final String refreshSetCookie;
    private final ManagerRepository managerRepository;
    private final CustomerRepository customerRepository;

    private static final String GRANT_TYPE = "Bearer ";
    private static final String REFRESH_TOKEN_SUB = "RefreshToken";

    /**
     * JWT token 생성 메서드
     */
    public String generateAccessToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        return GRANT_TYPE + Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .claim("uuid", principal.getUuid())
                .claim("role", principal.getRole())
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(Date.from(Instant.now().plusMillis(accessTokenExpire)))
                .compact();
    }

    public String generateRefreshToken() {

        return Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUB)
                .setExpiration(Date.from(Instant.now().plusMillis(refreshTokenExpire)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 클라이언트 요청에서 JWT token 추출 메서드
     * 이때 AccessToken 은 요청 header 에서, RefreshToken 은 cookie 에서 추출한다.
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.of(request.getHeader(accessHeader))
                .filter(token ->
                        token.startsWith(GRANT_TYPE))
                .map(token ->
                        token.replace(GRANT_TYPE, ""));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie ->
                        cookie.getName().equals(refreshSetCookie))
                .findFirst()
                .map(Cookie::getValue);
    }

    /**
     * AccessToken 복호화하고 Claims 가져오는 메서드
     */
    public Optional<Claims> parseClaims(String accessToken) {
        try {
            return Optional.of(Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody());
        } catch (ExpiredJwtException e) {
            return Optional.of(e.getClaims());
        } catch (JwtException e) {
            log.error(e.getMessage());
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            throw new BusinessException(Code.MEMBER_UNAUTHORIZED);
        }
    }

    /**
     * RefreshToken 업데이트 메서드
     */
    public void updateRefreshToken(String Uuid, String refreshToken, String role) {
        if (Role.MANAGER.getRole().equals(role)) {
            managerRepository.findByUuid(Uuid)
                    .ifPresentOrElse(
                            manager -> manager.updateRefreshToken(refreshToken),
                            () -> {
                        throw new BusinessException(Code.MANAGER_NOT_FOUND);
                            });

        }
        if (Role.USER.getRole().equals(role)) {
            customerRepository.findByUuid(Uuid)
                    .ifPresentOrElse(
                    customer -> customer.updateRefreshToken(refreshToken),
                    () -> {
                        throw new BusinessException(Code.CUSTOMER_NOT_FOUND);
                    }
            );
        }
    }

    /**
     * Token 유효성 검증 메서드
     */

    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. - {}", e.getMessage());
            return false;
        }
    }

    /**
     * Token 만료기간 검증 메서드
     */
    public boolean isExpired(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
