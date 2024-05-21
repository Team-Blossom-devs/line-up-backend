package com.blossom.lineup.jwt.filter;

import com.blossom.lineup.Member.CustomerRepository;
import com.blossom.lineup.Member.ManagerRepository;
import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Member.entity.Manager;
import com.blossom.lineup.Member.entity.Member;
import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.exceptions.BusinessException;
import com.blossom.lineup.jwt.config.JwtResponseConfigurer;
import com.blossom.lineup.jwt.core.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * JWT 인증 필터
 * "/api/sign-in/manager" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 1. AccessToken 이 유효한 경우 -> 인증 성공 처리, RefreshToken 재발급 하지 않는다.
 * 2. AccessToken 이 없거나 유효하지 않은 경우
 *      -> Cookie 에서 RefreshToken 을 추출하여 DB의 RefreshToken 과 비교한다.
 *      -> 일치하면 AccessToken 과 RefreshToken 재발급한다. 일치하지 않는다면 인증 실패 처리.
 *
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> filterPassUrl = List.of("/", "/favicon.ico", "/api/sign-in/manager","/login/oauth2/code/kakao", "/oauth2/authorization/kakao");

    private final JwtTokenProvider jwtTokenProvider;
    private final ManagerRepository managerRepository;
    private final CustomerRepository customerRepository;
    private final JwtResponseConfigurer jwtResponseConfigurer;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (filterPassUrl.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info(request.getRequestURI());

        // Request Header 에서 AccessToken 추출
        String accessToken = jwtTokenProvider.extractAccessToken(request)
                .orElseThrow(() -> new BusinessException(Code.MEMBER_LOGIN_REQUIRED));

        String refreshToken = null;

        // AccessToken 에서 Claims 추출
        Claims claims = jwtTokenProvider.parseClaims(accessToken)
                .orElseThrow(() -> new BusinessException(Code.MEMBER_UNAUTHORIZED));

        Member member = checkMember(claims);

        Authentication authentication = jwtTokenProvider.generateAuthentication(member);

        // AccessToken 만료 검사
        if (jwtTokenProvider.isExpired(accessToken)) {
            log.info("accessToken 만료");
            String role = member.getRole().getRole();
            String uuid = member.getUuid();

            // RefreshToken 이 유효한지 검사
             refreshToken = jwtTokenProvider.extractRefreshToken(request).stream()
                     .findAny()
                     .filter(jwtTokenProvider::isValid)
                     .filter(token -> !jwtTokenProvider.isExpired(token))
                     .orElseThrow(() -> new BusinessException(Code.MEMBER_LOGIN_REQUIRED));

            // DB에 저장된 RefreshToken 과 일치하는지 검사 후 RefreshToken 재발급
            if (Role.MANAGER.getRole().equals(role)) {

                Manager manager = (Manager) member;

                if (manager.getRefreshToken().equals(refreshToken)) {
                    refreshToken = jwtTokenProvider.generateRefreshToken();
                    manager.updateRefreshToken(refreshToken);
                } else throw new BusinessException(Code.MEMBER_UNAUTHORIZED);
            }

            if (Role.USER.getRole().equals(role)) {

                Customer customer = (Customer) member;

                if (customer.getRefreshToken().equals(refreshToken)) {
                    refreshToken = jwtTokenProvider.generateRefreshToken();
                    customer.updateRefreshToken(refreshToken);
                } else throw new BusinessException(Code.MEMBER_UNAUTHORIZED);
            }

            accessToken = jwtTokenProvider.generateAccessToken(authentication);

        }

        if (refreshToken == null){
            Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                .filter(cookies -> cookies.getName().equals("refreshToken"))
                .findFirst();
            if (cookie.isEmpty()){
                refreshToken = null;
            } else {
                refreshToken = cookie.get().getValue();
            }
        }

        // Token 응답 형식 설정
        jwtResponseConfigurer.configureTokenResponse(response, accessToken, refreshToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private Member checkMember(Claims claims) {
        String role = claims.get("role", String.class);
        String uuid = claims.get("uuid", String.class);

        if (role.equals(Role.USER.getRole()) || role.equals(Role.GUEST.getRole())) {
             return customerRepository.findByUuid(uuid).orElseThrow(() -> new BusinessException(Code.CUSTOMER_NOT_FOUND));
        }
        if (role.equals(Role.MANAGER.getRole())) {
            return managerRepository.findByUuid(uuid).orElseThrow(() -> new BusinessException(Code.MANAGER_NOT_FOUND));
        }

        throw new BusinessException(Code.MEMBER_UNAUTHORIZED);
    }
}
