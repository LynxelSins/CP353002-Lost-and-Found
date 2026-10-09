package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.dto.response.NotificationResponse;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.security.UserPrincipal;
import com.example.lostandfound.service.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ครอบคลุมฟีเจอร์ "รีเฟรชแบบ near-real-time" (คู่มือทดสอบ กลุ่มที่ 3: TC-06, TC-07)
 * หน้าเว็บอาศัย polling ที่ /api/notifications/unread-count (badge กระดิ่ง) และ
 * /api/notifications (list) เดิมมีแต่ NotificationServiceImplTest ระดับ service เท่านั้น
 * ไม่มีเทสที่ controller เลย
 */
@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private NotificationService notificationService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserRepository userRepository;

    private UUID userId;

    @BeforeEach
    void setUpLoggedInUser() {
        userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("me@test.com").role(UserRole.USER).build();
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void unreadCount_shouldReturn200_withCount() throws Exception {
        // TC-06: badge แจ้งเตือนต้องขึ้นเลขอัตโนมัติจากการ poll endpoint นี้
        when(notificationService.countUnread(userId)).thenReturn(3L);

        mockMvc.perform(get("/api/v1/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(3));
    }

    @Test
    void unreadCount_shouldReturnZero_whenNoUnread() throws Exception {
        when(notificationService.countUnread(userId)).thenReturn(0L);

        mockMvc.perform(get("/api/v1/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(0));
    }

    @Test
    void mine_shouldReturn200_withPagedNotifications() throws Exception {
        // TC-07: banner "มีรายการใหม่" อ่านจากรายการแจ้งเตือนล่าสุด
        NotificationResponse n = NotificationResponse.builder()
                .id(1L).message("มีคนขอเคลมประกาศของคุณ").read(false).build();
        Page<NotificationResponse> page = new PageImpl<>(List.of(n));
        when(notificationService.getMine(eq(userId), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].message").value("มีคนขอเคลมประกาศของคุณ"))
                .andExpect(jsonPath("$.data.content[0].read").value(false));
    }

    @Test
    void markRead_shouldReturn200() throws Exception {
        doNothing().when(notificationService).markAsRead(1L, userId);

        mockMvc.perform(patch("/api/v1/notifications/{id}/read", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("อ่านแล้ว"));
    }

    @Test
    void markAllRead_shouldReturn200_andClearBadge() throws Exception {
        // หลังกด banner ใน TC-07 badge ต้องหายไป
        doNothing().when(notificationService).markAllAsRead(userId);

        mockMvc.perform(patch("/api/v1/notifications/read-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("อ่านทั้งหมดแล้ว"));
    }

    @Test
    void unreadCount_shouldReturn401_whenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/v1/notifications/unread-count"))
                .andExpect(status().isUnauthorized());
    }
}
