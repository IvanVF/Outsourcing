package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.configurations.security.CustomUserDetails;
import com.antikryptonite.outsourcing.entities.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Набор методов для получения информации по пользователям
 */
public class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * @return id пользователя, который выполняет действие
     */
    public static UUID getUserId() {
        CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UUID.fromString(customUserDetails.getUsername());
    }

    /**
     * @return role пользователя
     */
    public static Role getRole() {
        CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GrantedAuthority grantedAuthority = new ArrayList<GrantedAuthority>(customUserDetails.getAuthorities()).get(0);
        return Role.valueOf(grantedAuthority.getAuthority().substring(5));
    }

    /**
     * @return true - если пользователь авторизован (не гость), иначе false
     */
    public static boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null &&
                ((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername() != null;
    }
}
