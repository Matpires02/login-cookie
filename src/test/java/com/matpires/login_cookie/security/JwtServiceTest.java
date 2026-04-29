package com.matpires.login_cookie.security;

import com.matpires.login_cookie.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Profile("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateAndValidateToken() {

        User user = new User();
        user.setEmail("test@email.com");

        String token = jwtService.generateAccessToken(user);

        assertNotNull(token);
        assertEquals("test@email.com", jwtService.extractEmail(token));
        assertTrue(jwtService.isTokenValid(token));
    }
}