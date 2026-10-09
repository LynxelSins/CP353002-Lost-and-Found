package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.dto.request.ChangePasswordRequest;
import com.example.lostandfound.dto.request.UpdateProfileRequest;
import com.example.lostandfound.dto.response.UserResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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

        mockMvc.perform(patch("/api/v1/users/me/password")
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

        mockMvc.perform(patch("/api/v1/users/me/password")
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

        mockMvc.perform(patch("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_shouldReturn401_whenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("newpass456");

        mockMvc.perform(patch("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removePassword_shouldReturn204() throws Exception {
        doNothing().when(userService).removePassword(userId);

        mockMvc.perform(delete("/api/v1/users/me/password"))
                .andExpect(status().isNoContent());
    }

    @Test
    void removePassword_shouldReturn400_whenNotGoogleLinked() throws Exception {
        doThrow(new BadRequestException("ไม่สามารถลบรหัสผ่านได้ เนื่องจากบัญชีนี้ยังไม่ได้เชื่อมต่อกับ Google"))
                .when(userService).removePassword(userId);

        mockMvc.perform(delete("/api/v1/users/me/password"))
                .andExpect(status().isBadRequest());
    }

    // ===== TC-01 / TC-02: มุมขวาบนต้องขึ้นรูปโปรไฟล์ที่อัปโหลดจริง (หรือ emoji ถ้ายังไม่มี) =====
    // เดิมไม่มีเทสของ GET/PATCH /api/users/me เลย ทั้งที่เป็น endpoint หลักของฟีเจอร์รูปโปรไฟล์

    @Test
    void getMe_shouldReturn200_withAvatarUrl_whenUploaded() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(userId).email("me@test.com").fullName("สมชาย")
                .avatarUrl("https://storage.googleapis.com/bucket/uploads/me.png")
                .role(UserRole.USER).build();
        when(userService.getCurrentUser(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.avatarUrl")
                        .value("https://storage.googleapis.com/bucket/uploads/me.png"));
    }

    @Test
    void getMe_shouldReturn200_withNullAvatarUrl_whenNeverUploaded() throws Exception {
        // TC-02: ผู้ใช้ที่ยังไม่มีรูปโปรไฟล์ -> avatarUrl เป็น null ให้ frontend โชว์ emoji
        UserResponse response = UserResponse.builder()
                .id(userId).email("me@test.com").fullName("สมชาย")
                .avatarUrl(null).role(UserRole.USER).build();
        when(userService.getCurrentUser(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.avatarUrl").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void updateProfile_shouldReturn200_andSaveAvatarUrl_afterUpload() throws Exception {
        // flow จริง: อัปโหลดรูปที่ POST /api/uploads เสร็จแล้วค่อยเซฟ URL ที่ได้มาไว้ในโปรไฟล์
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatarUrl("https://storage.googleapis.com/bucket/uploads/new.png");

        UserResponse response = UserResponse.builder()
                .id(userId).email("me@test.com").fullName("สมชาย")
                .avatarUrl("https://storage.googleapis.com/bucket/uploads/new.png")
                .role(UserRole.USER).build();
        when(userService.updateProfile(eq(userId), any(UpdateProfileRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("บันทึกโปรไฟล์เรียบร้อย"))
                .andExpect(jsonPath("$.data.avatarUrl")
                        .value("https://storage.googleapis.com/bucket/uploads/new.png"));
    }

    @Test
    void updateProfile_shouldReturn400_whenAvatarUrlTooLong() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatarUrl("a".repeat(501)); // @Size(max = 500) ใน UpdateProfileRequest

        mockMvc.perform(patch("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}