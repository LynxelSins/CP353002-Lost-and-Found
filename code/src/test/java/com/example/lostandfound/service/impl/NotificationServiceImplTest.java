package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.Notification;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.entity.UserProfile;
import com.example.lostandfound.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void notify_shouldNotThrow_whenProfileExists() {
        User user = User.builder().email("a@test.com")
                .profile(UserProfile.builder().fullName("สมชาย").build())
                .build();

        lenient().when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatCode(() -> notificationService.notify(user, "ทดสอบข้อความแจ้งเตือน"))
                .doesNotThrowAnyException();

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void notify_shouldNotThrow_whenProfileIsNull() {
        User user = User.builder().email("b@test.com").profile(null).build();

        lenient().when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatCode(() -> notificationService.notify(user, "ทดสอบข้อความแจ้งเตือน"))
                .doesNotThrowAnyException();

        verify(notificationRepository).save(any(Notification.class));
    }
}