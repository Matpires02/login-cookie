package com.matpires.login_cookie.controller;

import com.matpires.login_cookie.config.CookieUtil;
import com.matpires.login_cookie.dto.AuthResponseDto;
import com.matpires.login_cookie.dto.LoginRequestDto;
import com.matpires.login_cookie.dto.RegisterRequestDto;
import com.matpires.login_cookie.dto.TokenResponseDTO;
import com.matpires.login_cookie.service.AuthService;
import com.matpires.login_cookie.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final CookieUtil cookieUtil;
    private final UserService userService;

    public AuthController(AuthService service, CookieUtil cookieUtil, UserService userService) {
        this.service = service;
        this.cookieUtil = cookieUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request,
                                   HttpServletResponse response) {

        TokenResponseDTO tokens = service.login(request);

        cookieUtil.add(response, "access_token", tokens.getAccessToken(), 900);
        cookieUtil.add(response, "refresh_token", tokens.getRefreshToken(), 604800);

        return ResponseEntity.ok(new AuthResponseDto("Login realizado"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, HttpServletRequest request) {

        String acessToken = Arrays.stream(request.getCookies())
                .filter(c -> "access_token".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow();

        service.logout(acessToken);

        cookieUtil.delete(response, "access_token");
        cookieUtil.delete(response, "refresh_token");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(HttpServletRequest request,
                                     HttpServletResponse response) {

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow();

        var tokens = service.refresh(refreshToken);

        cookieUtil.add(response, "access_token", tokens.getAccessToken(), 900);
        cookieUtil.add(response, "refresh_token", tokens.getRefreshToken(), 604800);

        return ResponseEntity.ok(new AuthResponseDto("Refresh realizado"));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDto dto) {

        userService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
