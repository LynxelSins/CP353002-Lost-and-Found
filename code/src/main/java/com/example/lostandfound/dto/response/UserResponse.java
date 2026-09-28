package com.example.lostandfound.dto.response;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String email;
    private String fullName;
    private String phoneNumber;      // เพิ่มใหม่
    private String avatarUrl;
    private UserRole role;
    private boolean hasPassword;
    private boolean linkedToGoogle;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getProfile() != null ? user.getProfile().getFullName() : null)
                .phoneNumber(user.getProfile() != null ? user.getProfile().getPhoneNumber() : null) // เพิ่มใหม่
                .avatarUrl(user.getProfile() != null ? user.getProfile().getAvatarUrl() : null) // เพิ่มใหม่
                .role(user.getRole())
                .hasPassword(user.getPasswordHash() != null)
                .linkedToGoogle(user.getFirebaseUid() != null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
