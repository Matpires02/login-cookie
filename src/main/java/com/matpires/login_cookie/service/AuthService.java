package com.matpires.login_cookie.service;

import com.matpires.login_cookie.dto.LoginRequestDto;
import com.matpires.login_cookie.dto.TokenResponseDTO;
import com.matpires.login_cookie.entity.User;
import com.matpires.login_cookie.repository.UserRepository;
import com.matpires.login_cookie.security.JwtService;
import com.matpires.login_cookie.security.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder encoder, TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.encoder = encoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public TokenResponseDTO login(LoginRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        return generateTokens(user);
    }

    private TokenResponseDTO generateTokens(User user) {
        return new TokenResponseDTO(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    public TokenResponseDTO refresh(String refreshToken) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey("super-secret-key-super-secret-key".getBytes())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return generateTokens(user);
    }

    public void logout() {
        //tokenBlacklistService.blacklist(jti);
    }
}
