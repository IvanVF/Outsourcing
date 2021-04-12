package com.antikryptonite.outsourcing.configurations.security.jwt;

import com.antikryptonite.outsourcing.configurations.security.CustomUserDetails;
import com.antikryptonite.outsourcing.configurations.security.CustomUserDetailsService;
import com.antikryptonite.outsourcing.entities.Role;
import liquibase.pro.packaged.G;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static org.springframework.util.StringUtils.hasText;

/**
 * Фильтр, отвечающи за обработку JWT
 */
@Component
@Log
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Конструктор JWT
     *
     * @param jwtProvider - компонент для генерации и проверки JWT
     * @param customUserDetailsService - сервис для получения учетных данных пользователя
     */
    @Autowired
    public JwtFilter(JwtProvider jwtProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtProvider = jwtProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("do filter...");
        String token = getTokenFromRequest((HttpServletRequest) request);
        if (token != null && jwtProvider.validateToken(token)) {
            TokenInformation tokenInformation = jwtProvider.getTokenInformation(token);
            String userId = tokenInformation.getUserId();
            Role role = tokenInformation.getRole();
            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(userId);
            if (checkRole(role, customUserDetails.getAuthorities())) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Получение токена из хедера Authorization
     *
     * @param request - запрос
     * @return - возвращает найденный токен
     */
    private static String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private static boolean checkRole(Role role, Collection<? extends GrantedAuthority> realRoles) {
        if (realRoles.toArray().length != 1) {
            return false;
        }
        GrantedAuthority[] grantedAuthorities = new GrantedAuthority[1];
        return realRoles.toArray(grantedAuthorities)[0].getAuthority().equals("ROLE_" + role.name());
    }
}