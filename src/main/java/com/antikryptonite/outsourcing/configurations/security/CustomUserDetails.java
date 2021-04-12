package com.antikryptonite.outsourcing.configurations.security;

import com.antikryptonite.outsourcing.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Учетные данные пользователя
 */
public class CustomUserDetails implements UserDetails {

    private String userId;

    private String password;

    private Collection<? extends GrantedAuthority> grantedAuthorities;

    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * Конвертация сущности пользователя
     *
     * @param userEntity - параметры сущности пользователя
     * @return - конвертированная сущность пользователя
     */
    public static CustomUserDetails fromUserEntityToCustomUserDetails(UserEntity userEntity) {
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.userId = userEntity.getId().toString();
        userDetails.password = userEntity.getPassword();
        userDetails.grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX + userEntity.getRole().name()));
        return userDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
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