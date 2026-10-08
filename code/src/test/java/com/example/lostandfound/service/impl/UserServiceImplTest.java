package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.entity.UserProfile;
import com.example.lostandfound.dto.request.UpdateProfileRequest;
import com.example.lostandfound.dto.response.UserResponse;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void changePassword_shouldSucceed_whenNoExistingPassword() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).passwordHash(null).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass123")).thenReturn("hashedNew");

        userService.changePassword(userId, null, "newpass123");

        assertThat(user.getPasswordHash()).isEqualTo("hashedNew");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldThrow_whenCurrentPasswordWrong() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).passwordHash("hashedOld").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOld", "hashedOld")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(userId, "wrongOld", "newpass123"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("ปัจจุบันไม่ถูกต้อง");

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldThrow_whenNewPasswordBlank() {
        UUID userId = UUID.randomUUID();

        assertThatThrownBy(() -> userService.changePassword(userId, "old", "  "))
                .isInstanceOf(BadRequestException.class);

        verify(userRepository, never()).findById(any());
    }

    @Test
    void changePassword_shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(userId, "old", "newpass123"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void removePassword_shouldSucceed_whenGoogleLinked() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).firebaseUid("firebase-uid-123").passwordHash("hashed").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.removePassword(userId);

        assertThat(user.getPasswordHash()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void removePassword_shouldThrow_whenNotGoogleLinked() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).firebaseUid(null).passwordHash("hashed").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.removePassword(userId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Google");
    }

    // ===== TC-01 / TC-02: อัปโหลดรูปโปรไฟล์ (avatarUrl) =====
    // เดิมไม่มีเทสของ updateProfile / getCurrentUser เลย ทั้งที่เป็น method ที่ใช้จริงกับฟีเจอร์นี้

    @Test
    void updateProfile_shouldSaveAvatarUrl_whenProfileExists() {
        UUID userId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder().fullName("สมชาย").avatarUrl(null).build();
        User user = User.builder().id(userId).email("a@test.com").profile(profile).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatarUrl("https://storage.googleapis.com/bucket/uploads/a-avatar.png");

        UserResponse response = userService.updateProfile(userId, request);

        assertThat(user.getProfile().getAvatarUrl())
                .isEqualTo("https://storage.googleapis.com/bucket/uploads/a-avatar.png");
        assertThat(response.getAvatarUrl())
                .isEqualTo("https://storage.googleapis.com/bucket/uploads/a-avatar.png");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_shouldKeepExistingAvatar_whenAvatarUrlNotSent() {
        // partial update: ส่งมาแค่ fullName ไม่ควรไปลบ avatarUrl เดิมทิ้ง
        UUID userId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder()
                .fullName("สมชาย")
                .avatarUrl("https://storage.googleapis.com/bucket/uploads/old.png")
                .build();
        User user = User.builder().id(userId).email("a@test.com").profile(profile).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("สมชาย ใหม่");

        userService.updateProfile(userId, request);

        assertThat(user.getProfile().getAvatarUrl())
                .isEqualTo("https://storage.googleapis.com/bucket/uploads/old.png");
        assertThat(user.getProfile().getFullName()).isEqualTo("สมชาย ใหม่");
    }

    @Test
    void updateProfile_shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(userId, new UpdateProfileRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getCurrentUser_shouldReturnAvatarUrl_whenAlreadySet() {
        // TC-01: มุมขวาบนต้องเปลี่ยนจาก emoji เป็นรูปที่อัปโหลดจริง — endpoint ที่ใช้ดึงข้อมูลนี้
        UUID userId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder()
                .fullName("สมชาย")
                .avatarUrl("https://storage.googleapis.com/bucket/uploads/a-avatar.png")
                .build();
        User user = User.builder().id(userId).email("a@test.com").profile(profile).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.getCurrentUser(userId);

        assertThat(response.getAvatarUrl())
                .isEqualTo("https://storage.googleapis.com/bucket/uploads/a-avatar.png");
    }

    @Test
    void getCurrentUser_shouldReturnNullAvatarUrl_whenNeverUploaded() {
        // TC-02: ผู้ใช้ที่ยังไม่มีรูปโปรไฟล์ -> ต้องได้ null ไม่ใช่ path พัง เพื่อให้ frontend
        // ไปแสดง emoji เป็นค่า default แทน
        UUID userId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder().fullName("สมหญิง").avatarUrl(null).build();
        User user = User.builder().id(userId).email("nobody@test.com").profile(profile).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.getCurrentUser(userId);

        assertThat(response.getAvatarUrl()).isNull();
    }

    @Test
    void getCurrentUser_shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}