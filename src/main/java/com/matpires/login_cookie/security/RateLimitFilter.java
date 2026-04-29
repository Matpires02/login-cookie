package com.matpires.login_cookie.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if ("/auth/login".equals(path) && "POST".equalsIgnoreCase(request.getMethod())) {

            String ip = getClientIP(request);

            var bucket = rateLimitService.resolveBucket(ip);

            if (!bucket.tryConsume(1)) {
                response.setStatus(429);
                response.getWriter().write("Too many attempts. Try later.");
                return;
            }

            // 🔁 seguir fluxo
            filterChain.doFilter(request, response);

            // 🚨 Se falhou (401)
            if (response.getStatus() == 401) {
                rateLimitService.incrementFailedAttempts(ip);
            }

            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return (xfHeader == null) ? request.getRemoteAddr() : xfHeader.split(",")[0];
    }
}
