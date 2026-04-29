package com.matpires.login_cookie.service;

import com.matpires.login_cookie.dto.LoginRequestDto;
import com.matpires.login_cookie.dto.TokenResponseDTO;
import com.matpires.login_cookie.entity.User;
import com.matpires.login_cookie.exceptions.InvalidCredentialsException;
import com.matpires.login_cookie.repository.UserRepository;
import com.matpires.login_cookie.security.JwtService;
import com.matpires.login_cookie.security.RateLimitService;
import com.matpires.login_cookie.security.TokenBlacklistService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder encoder,
                       TokenBlacklistService tokenBlacklistService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.encoder = encoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public TokenResponseDTO login(LoginRequestDto request) {

        User user = userRepository.findByEmailAndActivatedTrue(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
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
        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return generateTokens(user);
    }

    public void logout(String token) {
        String jti = jwtService.extractJti(token);
        tokenBlacklistService.blacklist(jti);
    }
}
