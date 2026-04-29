package com.matpires.login_cookie.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Profile("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterUser() throws Exception {

        String body = """
                {
                    "email": "teste@email.com",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        // primeiro registra
        String register = """
                {
                    "email": "login@email.com",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(register));

        // depois faz login
        String login = """
                {
                    "email": "login@email.com",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"));
    }

    @Test
    void shouldFailLoginWrongPassword() throws Exception {

        String register = """
                {
                    "email": "fail@email.com",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(register));

        String login = """
                {
                    "email": "fail@email.com",
                    "password": "wrong"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailLoginWrongEmail() throws Exception {

        String login = """
                {
                    "email": "fail_email@email.com",
                    "password": "wrong"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isBadRequest());
    }

}
