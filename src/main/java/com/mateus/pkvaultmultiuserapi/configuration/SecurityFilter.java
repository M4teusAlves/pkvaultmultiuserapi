package com.mateus.pkvaultmultiuserapi.configuration;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mateus.pkvaultmultiuserapi.model.User;
import com.mateus.pkvaultmultiuserapi.service.TokenService;
import com.mateus.pkvaultmultiuserapi.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var token = recoverToken(request);

        if (token != null) {
            try {
                var subject = tokenService.validateToken(token);

                if (subject != null) {
                    UserDetails details = userService.findUserDetailsByLogin(subject);

                    if (details instanceof User user && user.isEnabled() && isSessionAlive(user)) {
                        var authentication = new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext(); // token inválido: segue sem autenticação
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSessionAlive(User user) {
        return user.getLastHeartbeat() != null
                && user.getLastHeartbeat().isAfter(LocalDateTime.now().minusSeconds(120));
    }

    public String recoverToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;
        for (Cookie c : cookies) {
            if ("token".equals(c.getName()))
                return c.getValue();
        }
        return null;
    }

}
