package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation ตั้งต้น: log อย่างเดียว (พอสำหรับ dev/เทส/เดโม)
 * จุดต่อขยาย (Open/Closed Principle): จะเปลี่ยนเป็นส่งอีเมล/LINE Notify/Push จริง
 * แค่เขียน implementation ใหม่ของ NotificationService แล้วสลับ Bean — ไม่ต้องแก้โค้ดที่เรียกใช้เลย
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void notify(User recipient, String message) {
        String target = recipient.getProfile() != null
                ? recipient.getProfile().getFullName()
                : recipient.getEmail();
        log.info("[NOTIFY] -> {} ({}): {}", target, recipient.getEmail(), message);
    }
}