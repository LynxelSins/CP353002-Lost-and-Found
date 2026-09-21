package com.example.lostandfound.controller.api;

import com.example.lostandfound.config.SecurityConfig;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.dto.request.LoginRequest;
import com.example.lostandfound.dto.request.RegisterRequest;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtAuthenticationFilter;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.security.RestAccessDeniedHandler;
import com.example.lostandfound.security.RestAuthenticationEntryPoint;
import com.example.lostandfound.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        JwtAuthenticationFilter.class // SecurityConfig ต้องการ bean นี้จริง ไม่ import จะ context load ไม่ขึ้น
})
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthService authService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserRepository userRepository;

    @Test
    void register_shouldReturn201_whenSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@test.com");
        request.setPassword("password123");
        request.setFullName("New User");

        User user = User.builder().email("new@test.com").role(UserRole.USER).build();
        when(authService.register(any(RegisterRequest.class))).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.accessToken").value("fake-jwt-token"));
    }

    @Test
    void register_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("dup@test.com");
        request.setPassword("password123");
        request.setFullName("Dup User");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new ConflictException("อีเมลนี้ถูกใช้งานแล้ว"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void register_shouldReturn400_whenValidationFails() throws Exception {
        RegisterRequest request = new RegisterRequest(); // ไม่กรอกอะไรเลย -> validation fail

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn200_whenSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("a@test.com");
        request.setPassword("password123");

        User user = User.builder().email("a@test.com").role(UserRole.USER).build();
        when(authService.login("a@test.com", "password123")).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("fake-jwt-token"));
    }
}