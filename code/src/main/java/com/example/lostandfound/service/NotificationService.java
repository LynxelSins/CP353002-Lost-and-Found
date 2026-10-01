package com.example.lostandfound.service;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    void notify(User recipient, String message);

    Page<NotificationResponse> getMine(UUID userId, Pageable pageable);

    long countUnread(UUID userId);

    void markAsRead(Long notificationId, UUID userId);

    void markAllAsRead(UUID userId);
}