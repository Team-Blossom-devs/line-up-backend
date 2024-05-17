package com.blossom.lineup.oauth2;

import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.oauth2.userInfo.KakaoOAuth2UserInfo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.UUID;

@Getter
public class KakaoOAuthAttributes {

    private final String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private final KakaoOAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Builder
    private KakaoOAuthAttributes(String nameAttributeKey, KakaoOAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static KakaoOAuthAttributes of(String userNameAttributeName, Map<String, Object> attributes) {
        return KakaoOAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public Customer toEntity(KakaoOAuth2UserInfo oauth2UserInfo) {
        return Customer.builder()
                .userName(oauth2UserInfo.getNickname())
                .phoneNumber(UUID.randomUUID().toString())
                .role(Role.GUEST)
                .email(oauth2UserInfo.getEmail())
                .socialId(oauth2UserInfo.getId())
                .build();
    }
}
