package com.blossom.lineup.oauth2.userInfo;

import com.blossom.lineup.oauth2.userInfo.OAuth2UserInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public Long getId() {
        return (Long) attributes.get("id");
    }

    @Override
    public String getNickname() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        if (profile == null) return null;

        return (String) profile.get("nickname");
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) return null;

        boolean isEmailVerified = (boolean) kakaoAccount.get("is_email_verified");
        boolean isEmailValid = (boolean) kakaoAccount.get("is_email_valid");

        if (!isEmailValid || !isEmailVerified) return null;

        return (String) kakaoAccount.get("email");
    }
}
