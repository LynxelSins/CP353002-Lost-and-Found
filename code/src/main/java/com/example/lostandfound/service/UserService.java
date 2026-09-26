package com.example.lostandfound.service;

import com.example.lostandfound.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse getCurrentUser(UUID userId);

    void changePassword(UUID userId, String currentPassword, String newPassword);

    void removePassword(UUID userId);
}