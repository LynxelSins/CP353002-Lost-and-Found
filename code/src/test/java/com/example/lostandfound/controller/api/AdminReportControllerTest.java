package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.security.UserPrincipal;
import com.example.lostandfound.service.ReportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminReportController.class)
@AutoConfigureMockMvc(addFilters = false) // ข้าม filter chain แต่ @PreAuthorize (method security) ยังทำงานอยู่
@Import(AdminReportControllerTest.MethodSecurityTestConfig.class)
class AdminReportControllerTest {

    // เปิด method security เฉพาะในเทสนี้ เพราะ @EnableMethodSecurity อยู่ใน SecurityConfig ตัวจริง
    // ซึ่ง @WebMvcTest ไม่โหลดให้อัตโนมัติ
    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ReportService reportService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserRepository userRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(UserRole role) {
        User user = User.builder().id(UUID.randomUUID()).email("x@test.com").role(role).build();
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @Test
    void delete_shouldReturn200_whenUserIsStaff() throws Exception {
        loginAs(UserRole.STAFF);

        mockMvc.perform(delete("/api/admin/reports/{id}", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturn403_whenUserIsNotStaff() throws Exception {
        loginAs(UserRole.USER);

        mockMvc.perform(delete("/api/admin/reports/{id}", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_shouldReturn403_whenNotAuthenticatedAtAll() throws Exception {
        // ไม่ login เลย -> AuthenticationCredentialsNotFoundException ก็ถูก
        // AccessDeniedException handler ครอบคลุมด้วย (Spring แปลงให้เป็น Denied เมื่อไม่มี auth ในบาง config)
        mockMvc.perform(delete("/api/admin/reports/{id}", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }
}