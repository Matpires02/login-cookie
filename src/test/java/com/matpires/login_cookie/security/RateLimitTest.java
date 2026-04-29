package com.matpires.login_cookie.security;

import com.matpires.login_cookie.entity.User;
import com.matpires.login_cookie.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Profile("test")
class RateLimitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeAll() {
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("rate@email.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("123456"));

        userRepository.save(user);
    }

    @Test
    void shouldBlockAfterManyAttempts() throws Exception {

        String body = """
                {
                    "email": "rate@email.com",
                    "password": "wrong"
                }
                """;

        // várias tentativas erradas
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));
        }

        // deve bloquear
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isTooManyRequests());
    }
}