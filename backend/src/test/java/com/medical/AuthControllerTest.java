package com.medical;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.dto.AuthDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void login_validAdmin_returnsToken() throws Exception {
        var req = new AuthDto.LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        var req = new AuthDto.LoginRequest();
        req.setUsername("admin");
        req.setPassword("wrongpassword");

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_newUser_returnsOk() throws Exception {
        var req = new AuthDto.RegisterRequest();
        req.setUsername("newuser_test");
        req.setPassword("pass1234");
        req.setEmail("newuser@test.com");
        req.setRole("PATIENT");

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void register_duplicateUsername_returns409() throws Exception {
        var req = new AuthDto.RegisterRequest();
        req.setUsername("admin");
        req.setPassword("pass1234");
        req.setEmail("another@test.com");
        req.setRole("PATIENT");

        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }
}
