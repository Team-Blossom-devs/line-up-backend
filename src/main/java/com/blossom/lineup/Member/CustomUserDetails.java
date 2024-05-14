package com.blossom.lineup.Member;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String uuid;
    private final String role;

    @Builder(builderMethodName = "userDetailsBuilder")
    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, String uuid, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.uuid = uuid;
        this.role = role;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
