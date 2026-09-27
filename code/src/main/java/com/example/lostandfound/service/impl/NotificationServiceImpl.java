package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.Notification;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.dto.response.NotificationResponse;
import com.example.lostandfound.repository.NotificationRepository;
import com.example.lostandfound.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * เดิม implementation นี้ log อย่างเดียว ตอนนี้บันทึกลงตาราง notifications จริงด้วย
 * เพื่อให้หน้าเว็บดึงมาแสดงเป็นกระดิ่งแจ้งเตือนได้
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void notify(User recipient, String message) {
        String target = recipient.getProfile() != null
                ? recipient.getProfile().getFullName()
                : recipient.getEmail();
        log.info("[NOTIFY] -> {} ({}): {}", target, recipient.getEmail(), message);

        notificationRepository.save(Notification.builder()
                .recipient(recipient)
                .message(message)
                .read(false)
                .build());
    }

    @Override
    public Page<NotificationResponse> getMine(UUID userId, Pageable pageable) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .message(n.getMessage())
                        .read(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .build());
    }

    @Override
    public long countUnread(UUID userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, UUID userId) {
        notificationRepository.markAsRead(notificationId, userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }
}