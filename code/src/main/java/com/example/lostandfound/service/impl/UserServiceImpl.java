package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.dto.request.UpdateProfileRequest;
import com.example.lostandfound.dto.response.UserResponse; // <-- เพิ่ม import นี้
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // =========================================================================
    // เพิ่มเมธอดนี้เข้าไปเพื่อแก้ปัญหา Build Error บน CI
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "user_id", userId));
        return UserResponse.fromEntity(user);
    }

        @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "user_id", userId));

        if (user.getProfile() == null) {
            throw new ResourceNotFoundException("UserProfile", "user_id", userId);
        }

        user.getProfile().setFullName(request.getFullName());
        user.getProfile().setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user);

        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        if (!StringUtils.hasText(newPassword)) {
            throw new BadRequestException("กรุณากรอกรหัสผ่านใหม่");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "user_id", userId));

        boolean hasExistingPassword = StringUtils.hasText(user.getPasswordHash());

        if (hasExistingPassword) {
            if (!StringUtils.hasText(currentPassword)
                    || !passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                throw new BadRequestException("รหัสผ่านปัจจุบันไม่ถูกต้อง");
            }
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removePassword(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "user_id", userId));

        if (!StringUtils.hasText(user.getFirebaseUid())) {
            throw new BadRequestException(
                    "ไม่สามารถลบรหัสผ่านได้ เนื่องจากบัญชีนี้ยังไม่ได้เชื่อมต่อกับ Google");
        }

        user.setPasswordHash(null);
        userRepository.save(user);
    }
}