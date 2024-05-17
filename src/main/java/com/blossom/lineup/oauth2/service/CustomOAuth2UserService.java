package com.blossom.lineup.oauth2.service;

import com.blossom.lineup.Member.CustomerRepository;
import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.oauth2.CustomOAuth2User;
import com.blossom.lineup.oauth2.KakaoOAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>  {

    private final CustomerRepository customerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attributes = oAuth2User.getAttributes(); // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)

        KakaoOAuthAttributes extractAttributes = KakaoOAuthAttributes.of(userNameAttributeName, attributes);

        Customer createdUser = getUser(extractAttributes);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getRole().getRole())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser
        );

    }

    private Customer getUser(KakaoOAuthAttributes attributes) {
        Customer findCustomer = customerRepository.findBySocialId(attributes.getOauth2UserInfo().getId()).orElse(null);

        if(findCustomer == null) {
            return saveUser(attributes);
        }
        return findCustomer;
    }

    private Customer saveUser(KakaoOAuthAttributes attributes) {
        Customer createCustomer = attributes.toEntity(attributes.getOauth2UserInfo());
        return customerRepository.save(createCustomer);
    }
}
