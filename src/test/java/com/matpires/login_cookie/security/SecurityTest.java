package com.matpires.login_cookie.security;

import com.matpires.login_cookie.config.CookieUtil;
import com.matpires.login_cookie.entity.Role;
import com.matpires.login_cookie.entity.User;
import com.matpires.login_cookie.enums.RoleName;
import com.matpires.login_cookie.repository.RoleRepository;
import com.matpires.login_cookie.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Profile("test")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CookieUtil cookieUtil;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void beforeAll() {
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("test_security@email.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRoles(Set.of(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow()));

        User userAdm = new User();
        userAdm.setEmail("test_security_adm@email.com");
        userAdm.setActivated(true);
        userAdm.setPassword(passwordEncoder.encode("123456"));
        userAdm.setRoles(Set.of(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(), roleRepository.findByName(RoleName.ROLE_USER).orElseThrow()));

        userRepository.saveAll(List.of(user, userAdm));
    }

    @Test
    void shouldDenyAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowWithValidToken() throws Exception {

        String login = """
                {
                    "email": "test_security@email.com",
                    "password": "123456"
                }
                """;

        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(login)).andReturn();

        Cookie[] cookies = mvcResult.getResponse().getCookies();

        mockMvc.perform(get("/user")
                        .cookie(cookies))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyWithValidTokenInAdminPath() throws Exception {

        String login = """
                {
                    "email": "test_security@email.com",
                    "password": "123456"
                }
                """;

        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(login)).andReturn();

        Cookie[] cookies = mvcResult.getResponse().getCookies();

        mockMvc.perform(get("/admin")
                        .cookie(cookies))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowWithValidTokenAdmin() throws Exception {

        String login = """
                {
                    "email": "test_security_adm@email.com",
                    "password": "123456"
                }
                """;

        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(login)).andReturn();

        Cookie[] cookies = mvcResult.getResponse().getCookies();

        mockMvc.perform(get("/admin")
                        .cookie(cookies))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user")
                        .cookie(cookies))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAccessAfterLogout() throws Exception {
        String login = """
                {
                    "email": "test_security@email.com",
                    "password": "123456"
                }
                """;

        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(login)).andReturn();

        // Pegando cookies
        Cookie[] cookies = mvcResult.getResponse().getCookies();

        // Deslogando
        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(login).cookie(cookies));

        mockMvc.perform(get("/user")
                        .cookie(cookies))
                .andExpect(status().isUnauthorized());
    }
}
