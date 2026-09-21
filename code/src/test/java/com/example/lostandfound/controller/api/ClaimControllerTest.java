package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ClaimStatus;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.dto.response.ClaimResponse;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.security.UserPrincipal;
import com.example.lostandfound.service.ClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClaimController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClaimControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ClaimService claimService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserRepository userRepository;

    private UUID userId;

    @BeforeEach
    void setUpLoggedInUser() {
        userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("claimant@test.com").role(UserRole.USER).build();
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void submit_shouldReturn201_whenValid() throws Exception {
        UUID reportId = UUID.randomUUID();
        SubmitClaimRequest request = new SubmitClaimRequest();
        request.setEvidenceText("เจอกระเป๋าสีดำที่โรงอาหาร มีสติกเกอร์แมวติดอยู่");

        ClaimResponse response = ClaimResponse.builder()
                .id(UUID.randomUUID()).reportId(reportId).claimantId(userId)
                .claimStatus(ClaimStatus.PENDING).build();

        when(claimService.submit(eq(reportId), eq(userId), any(SubmitClaimRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/reports/{reportId}/claims", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.claimStatus").value("PENDING"));
    }

    @Test
    void submit_shouldReturn409_whenDuplicatePendingClaim() throws Exception {
        UUID reportId = UUID.randomUUID();
        SubmitClaimRequest request = new SubmitClaimRequest();
        request.setEvidenceText("เจอกระเป๋าสีดำที่โรงอาหาร มีสติกเกอร์แมวติดอยู่");

        when(claimService.submit(eq(reportId), eq(userId), any(SubmitClaimRequest.class)))
                .thenThrow(new ConflictException("คุณมีคำขอเคลมที่รอตรวจสอบอยู่กับประกาศนี้แล้ว"));

        mockMvc.perform(post("/api/reports/{reportId}/claims", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void submit_shouldReturn400_whenEvidenceTextBlank() throws Exception {
        UUID reportId = UUID.randomUUID();
        SubmitClaimRequest request = new SubmitClaimRequest(); // evidenceText ว่าง

        mockMvc.perform(post("/api/reports/{reportId}/claims", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listForReport_shouldReturn200_withList() throws Exception {
        UUID reportId = UUID.randomUUID();
        ClaimResponse claim = ClaimResponse.builder().id(UUID.randomUUID()).reportId(reportId).build();
        when(claimService.getByReport(reportId, userId)).thenReturn(List.of(claim));

        mockMvc.perform(get("/api/reports/{reportId}/claims", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void approve_shouldReturn200() throws Exception {
        UUID claimId = UUID.randomUUID();
        ClaimResponse response = ClaimResponse.builder().id(claimId).claimStatus(ClaimStatus.APPROVED).build();
        when(claimService.approve(eq(claimId), eq(userId), any())).thenReturn(response);

        mockMvc.perform(patch("/api/claims/{claimId}/approve", claimId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.claimStatus").value("APPROVED"));
    }

    @Test
    void reject_shouldReturn200() throws Exception {
        UUID claimId = UUID.randomUUID();
        ClaimResponse response = ClaimResponse.builder().id(claimId).claimStatus(ClaimStatus.REJECTED).build();
        when(claimService.reject(claimId, userId)).thenReturn(response);

        mockMvc.perform(patch("/api/claims/{claimId}/reject", claimId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.claimStatus").value("REJECTED"));
    }
}