package com.matpires.login_cookie.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtAuthConverter converter;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final RateLimitService rateLimitService;

    public JwtFilter(JwtAuthConverter converter, JwtService jwtService, TokenBlacklistService tokenBlacklistService, RateLimitService rateLimitService) {
        this.converter = converter;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        // 🔓 Permitir endpoints públicos sem bloquear
        String path = request.getRequestURI();

        if (path.startsWith("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractCookie(request, "access_token");

        String id = getClientIP(request);

        if (token == null || !jwtService.isTokenValid(token)) {
            rateLimitService.incrementFailedAttempts(id);
            chain.doFilter(request, response);
            return;
        }
        try {
            String username = jwtService.extractEmail(token);
            String jti = jwtService.extractJti(token);

            // 🚨 PROTEÇÃO REPLAY ATTACK
            if (tokenBlacklistService.isBlacklisted(jti)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido (replay detectado)");
                rateLimitService.incrementFailedAttempts(id);
                return;
            }


            // 👤 Extrair roles do token (SEM consultar banco)
            List<String> roles = jwtService.extractRoles(token);


            UsernamePasswordAuthenticationToken auth = converter.convert(username, roles);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            rateLimitService.reset(id);

        } catch (Exception e) {
            rateLimitService.incrementFailedAttempts(id);
            // ⚠️ Evita quebrar a aplicação por erro de token
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return (xfHeader == null)
                ? request.getRemoteAddr()
                : xfHeader.split(",")[0];
    }
}
