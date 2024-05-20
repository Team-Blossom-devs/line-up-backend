package com.blossom.lineup.handler;

import com.blossom.lineup.Member.CustomUserDetails;
import com.blossom.lineup.Member.ManagerRepository;
import com.blossom.lineup.Member.entity.Manager;
import com.blossom.lineup.Member.entity.dto.ManagerSignInResponse;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.Response;
import com.blossom.lineup.base.exceptions.BusinessException;
import com.blossom.lineup.jwt.config.JwtConfiguration;
import com.blossom.lineup.jwt.config.JwtResponseConfigurer;
import com.blossom.lineup.jwt.core.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.blossom.lineup.filter.ExceptionHandlerFilter.objectMapper;

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

        Manager manager = managerRepository.findByManagerName(userName).orElseThrow(() -> new BusinessException(Code.MANAGER_NOT_FOUND));
        manager.updateRefreshToken(refreshToken);

        Long organizationId = manager.getOrganization().getId();
        String role = manager.getRole().getRole();

        ManagerSignInResponse signInResponse = new ManagerSignInResponse(organizationId, role);

        try {
            String responseBody = objectMapper.writeValueAsString(Response.ok(signInResponse));
            response.getWriter().write(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("로그인에 성공하였습니다. managerName : {}", userName);
        log.info("로그인에 성공하였습니다. AccessToken : {}, refreshToken : {}", accessToken, refreshToken);
        log.info("발급된 AccessToken 만료 기간 : {} 분", TimeUnit.MILLISECONDS.toMinutes(jwtConfiguration.getAccessExpiration()));
    }

    private String extractUsername(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}