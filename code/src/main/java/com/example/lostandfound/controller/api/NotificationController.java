package com.example.lostandfound.controller.api;

import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.dto.response.NotificationResponse;
import com.example.lostandfound.security.CurrentUser;
import com.example.lostandfound.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<Page<NotificationResponse>> mine(
            @CurrentUser UUID userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ApiResponse.success(notificationService.getMine(userId, pageable));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> unreadCount(@CurrentUser UUID userId) {
        return ApiResponse.success(Map.of("count", notificationService.countUnread(userId)));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id, @CurrentUser UUID userId) {
        notificationService.markAsRead(id, userId);
        return ApiResponse.success("อ่านแล้ว", null);
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllRead(@CurrentUser UUID userId) {
        notificationService.markAllAsRead(userId);
        return ApiResponse.success("อ่านทั้งหมดแล้ว", null);
    }
}