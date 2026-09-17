package com.example.lostandfound.service;

import java.util.UUID;

public interface UserService {

    void changePassword(UUID userId, String currentPassword, String newPassword);

    void removePassword(UUID userId);
}