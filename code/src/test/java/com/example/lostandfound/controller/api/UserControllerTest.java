package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.dto.request.ChangePasswordRequest;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.security.UserPrincipal;
import com.example.lostandfound.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserRepository userRepository;

    private UUID userId;

    @BeforeEach
    void setUpLoggedInUser() {
        userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("me@test.com").role(UserRole.USER).build();
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void changePassword_shouldReturn200_whenValid() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldpass123");
        request.setNewPassword("newpass456");

        doNothing().when(userService).changePassword(eq(userId), eq("oldpass123"), eq("newpass456"));

        mockMvc.perform(patch("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("เปลี่ยนรหัสผ่านเรียบร้อย"));
    }

    @Test
    void changePassword_shouldReturn400_whenNewPasswordTooShort() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldpass123");
        request.setNewPassword("123"); // สั้นกว่า 8 ตัว -> validation fail

        mockMvc.perform(patch("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_shouldReturn400_whenCurrentPasswordWrong() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongOld");
        request.setNewPassword("newpass456");

        doThrow(new BadRequestException("รหัสผ่านปัจจุบันไม่ถูกต้อง"))
                .when(userService).changePassword(eq(userId), eq("wrongOld"), eq("newpass456"));

        mockMvc.perform(patch("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_shouldReturn401_whenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("newpass456");

        mockMvc.perform(patch("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removePassword_shouldReturn200() throws Exception {
        doNothing().when(userService).removePassword(userId);

        mockMvc.perform(delete("/api/users/me/password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ลบรหัสผ่านเรียบร้อย (เข้าสู่ระบบได้ด้วย Google เท่านั้น)"));
    }

    @Test
    void removePassword_shouldReturn400_whenNotGoogleLinked() throws Exception {
        doThrow(new BadRequestException("ไม่สามารถลบรหัสผ่านได้ เนื่องจากบัญชีนี้ยังไม่ได้เชื่อมต่อกับ Google"))
                .when(userService).removePassword(userId);

        mockMvc.perform(delete("/api/users/me/password"))
                .andExpect(status().isBadRequest());
    }
}