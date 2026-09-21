package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.dto.request.CreateReportRequest;
import com.example.lostandfound.dto.response.ReportResponse;
import com.example.lostandfound.dto.response.ReportSummaryResponse;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.security.UserPrincipal;
import com.example.lostandfound.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false) // ข้าม JWT filter จริง — จำลอง login เองผ่าน SecurityContextHolder
class ReportControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ReportService reportService;
    @MockitoBean private JwtUtil jwtUtil;               // dependency ของ JwtAuthenticationFilter
    @MockitoBean private UserRepository userRepository;  // dependency ของ JwtAuthenticationFilter

    private UUID userId;

    @BeforeEach
    void setUpLoggedInUser() {
        userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("owner@test.com").role(UserRole.USER).build();
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_shouldReturn201_whenValidRequest() throws Exception {
        CreateReportRequest request = new CreateReportRequest();
        request.setType(ReportType.LOST);
        request.setTitle("ทำกระเป๋าสตางค์หาย");
        request.setLocationName("โรงอาหารตึกวิทย์");
        request.setEventTimestamp(LocalDateTime.now());

        ReportResponse response = ReportResponse.builder()
                .id(UUID.randomUUID()).type(ReportType.LOST).title(request.getTitle())
                .status(ReportStatus.OPEN).ownerId(userId).build();

        when(reportService.create(eq(userId), any(CreateReportRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("ทำกระเป๋าสตางค์หาย"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    @Test
    void create_shouldReturn400_whenTitleBlank() throws Exception {
        CreateReportRequest request = new CreateReportRequest();
        request.setType(ReportType.LOST);
        request.setTitle(""); // blank -> validation fail
        request.setLocationName("ที่ไหนสักแห่ง");
        request.setEventTimestamp(LocalDateTime.now());

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn401_whenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext(); // จำลองไม่ได้ login

        CreateReportRequest request = new CreateReportRequest();
        request.setType(ReportType.LOST);
        request.setTitle("ของหาย");
        request.setLocationName("ที่ไหนสักแห่ง");
        request.setEventTimestamp(LocalDateTime.now());

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void search_shouldReturn200_withPagedResult() throws Exception {
        ReportSummaryResponse summary = ReportSummaryResponse.builder()
                .id(UUID.randomUUID()).type(ReportType.FOUND).title("เจอกุญแจ")
                .status(ReportStatus.OPEN).build();
        when(reportService.search(eq(ReportType.FOUND), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(summary), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/reports").param("type", "FOUND"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("เจอกุญแจ"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(reportService.getById(id)).thenThrow(new ResourceNotFoundException("Report", "id", id));

        mockMvc.perform(get("/api/reports/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void watch_shouldReturn200() throws Exception {
        UUID reportId = UUID.randomUUID();

        mockMvc.perform(post("/api/reports/{id}/watch", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ติดตามประกาศนี้แล้ว"));
    }

    @Test
    void unwatch_shouldReturn200() throws Exception {
        UUID reportId = UUID.randomUUID();

        mockMvc.perform(delete("/api/reports/{id}/watch", reportId))
                .andExpect(status().isOk());
    }

    @Test
    void close_shouldReturn200() throws Exception {
        UUID reportId = UUID.randomUUID();

        mockMvc.perform(post("/api/reports/{id}/close", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ปิดเคสเรียบร้อย"));
    }
}