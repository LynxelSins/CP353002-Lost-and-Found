package com.example.lostandfound.integration;

import com.example.lostandfound.AbstractIntegrationTest;
import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.dto.request.ApproveClaimRequest;
import com.example.lostandfound.dto.request.CreateReportRequest;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.dto.response.ClaimResponse;
import com.example.lostandfound.dto.response.ReportResponse;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.exception.ForbiddenException;
import com.example.lostandfound.repository.ReportRepository;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.service.ClaimService;
import com.example.lostandfound.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test แบบ end-to-end ผ่านฐานข้อมูล PostgreSQL จริง (Testcontainers)
 * ไม่ mock Repository เลย — ยิง service จริงลง DB จริง ครอบคลุม flow หลักตามเอกสาร:
 * โพสต์ประกาศ (OPEN) -> ยื่นเคลม (MATCH_PENDING) -> อนุมัติ (CLAIMED) -> ปิดเคส (CLOSED)
 * และ flow รอง: ปฏิเสธเคลมที่เหลือทั้งหมด -> กลับเป็น OPEN อัตโนมัติ
 */
@Tag("integration")
class ClaimFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired private ReportService reportService;
    @Autowired private ClaimService claimService;
    @Autowired private UserRepository userRepository;
    @Autowired private ReportRepository reportRepository;

    private User owner;
    private User claimant;

    @BeforeEach
    void setUpUsers() {
        owner = userRepository.save(User.builder()
                .email("owner-" + UUID.randomUUID() + "@test.com")
                .passwordHash("hashed")
                .role(UserRole.USER)
                .build());

        claimant = userRepository.save(User.builder()
                .email("claimant-" + UUID.randomUUID() + "@test.com")
                .passwordHash("hashed")
                .role(UserRole.USER)
                .build());
    }

    private CreateReportRequest buildCreateReportRequest() {
        CreateReportRequest request = new CreateReportRequest();
        request.setType(ReportType.LOST);
        request.setTitle("ทำกระเป๋าสตางค์หายที่โรงอาหาร");
        request.setLocationName("โรงอาหารตึกวิทยาศาสตร์");
        request.setEventTimestamp(LocalDateTime.now().minusHours(2));
        return request;
    }

    private SubmitClaimRequest buildSubmitClaimRequest() {
        SubmitClaimRequest request = new SubmitClaimRequest();
        // LOST report -> ClaimEligibilityStrategy บังคับ evidenceText อย่างน้อย 10 ตัวอักษร
        request.setEvidenceText("เจอกระเป๋าสตางค์สีน้ำตาลตกอยู่ใต้โต๊ะแถวประตูทางเข้า");
        return request;
    }

    @Test
    void fullLifecycle_shouldTransitionReportThroughAllStatuses_whenClaimApproved() {
        // ---------- ขั้นที่ 1: เจ้าของโพสต์ประกาศ -> ต้องเป็น OPEN ----------
        ReportResponse created = reportService.create(owner.getId(), buildCreateReportRequest());
        assertThat(created.getStatus()).isEqualTo(ReportStatus.OPEN);
        assertThat(created.getOwnerId()).isEqualTo(owner.getId());

        UUID reportId = created.getId();

        // ---------- ขั้นที่ 2: มีคนยื่นเคลม -> OPEN ต้องเปลี่ยนเป็น MATCH_PENDING ทันที ----------
        ClaimResponse claim = claimService.submit(reportId, claimant.getId(), buildSubmitClaimRequest());
        assertThat(claim.getClaimantId()).isEqualTo(claimant.getId());

        ReportResponse afterSubmit = reportService.getById(reportId);
        assertThat(afterSubmit.getStatus()).isEqualTo(ReportStatus.MATCH_PENDING);

        // ---------- ขั้นที่ 3: เจ้าของอนุมัติเคลม -> report ต้องกลายเป็น CLAIMED ----------
        ApproveClaimRequest approveRequest = new ApproveClaimRequest();
        approveRequest.setMeetingLocation("ล็อบบี้ตึกวิทย์ ชั้น 1");
        approveRequest.setMeetingTime(LocalDateTime.now().plusDays(1));

        ClaimResponse approved = claimService.approve(claim.getId(), owner.getId(), approveRequest);
        assertThat(approved.getClaimStatus().name()).isEqualTo("APPROVED");
        assertThat(approved.getMeetingLocation()).isEqualTo("ล็อบบี้ตึกวิทย์ ชั้น 1");

        ReportResponse afterApprove = reportService.getById(reportId);
        assertThat(afterApprove.getStatus()).isEqualTo(ReportStatus.CLAIMED);

        // ---------- ขั้นที่ 4: เจ้าของปิดเคส -> report ต้องกลายเป็น CLOSED ----------
        reportService.closeReport(reportId, owner.getId());

        Report closedEntity = reportRepository.findById(reportId).orElseThrow();
        assertThat(closedEntity.getStatus()).isEqualTo(ReportStatus.CLOSED);
    }

    @Test
    void reportShouldReopenToOpen_whenTheOnlyPendingClaimIsRejected() {
        ReportResponse created = reportService.create(owner.getId(), buildCreateReportRequest());
        UUID reportId = created.getId();

        ClaimResponse claim = claimService.submit(reportId, claimant.getId(), buildSubmitClaimRequest());
        assertThat(reportService.getById(reportId).getStatus()).isEqualTo(ReportStatus.MATCH_PENDING);

        // เจ้าของปฏิเสธเคลมเดียวที่มีอยู่ -> ไม่เหลือ PENDING/APPROVED เลย -> ต้องกลับเป็น OPEN อัตโนมัติ
        ClaimResponse rejected = claimService.reject(claim.getId(), owner.getId());
        assertThat(rejected.getClaimStatus().name()).isEqualTo("REJECTED");

        ReportResponse afterReject = reportService.getById(reportId);
        assertThat(afterReject.getStatus()).isEqualTo(ReportStatus.OPEN);
    }

    @Test
    void submit_shouldThrowConflict_whenSameUserSubmitsSecondPendingClaim() {
        ReportResponse created = reportService.create(owner.getId(), buildCreateReportRequest());
        UUID reportId = created.getId();

        claimService.submit(reportId, claimant.getId(), buildSubmitClaimRequest());

        // ยื่นซ้ำระหว่างที่ใบแรกยังค้าง PENDING -> ต้องโดน 409 (ConflictException) ไม่ใช่ 400
        assertThatThrownBy(() -> claimService.submit(reportId, claimant.getId(), buildSubmitClaimRequest()))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void closeReport_shouldThrowForbidden_whenCalledByNonOwner() {
        ReportResponse created = reportService.create(owner.getId(), buildCreateReportRequest());
        UUID reportId = created.getId();

        ClaimResponse claim = claimService.submit(reportId, claimant.getId(), buildSubmitClaimRequest());
        claimService.approve(claim.getId(), owner.getId(), new ApproveClaimRequest());

        // ผู้ยื่นเคลม (ไม่ใช่เจ้าของ) พยายามปิดเคสเอง -> ต้องโดนบล็อก
        assertThatThrownBy(() -> reportService.closeReport(reportId, claimant.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void closeReport_shouldThrowBadRequest_whenReportNotYetClaimed() {
        ReportResponse created = reportService.create(owner.getId(), buildCreateReportRequest());

        // ยังไม่มีใครเคลม สถานะยังเป็น OPEN -> ปิดเคสไม่ได้
        assertThatThrownBy(() -> reportService.closeReport(created.getId(), owner.getId()))
                .isInstanceOf(BadRequestException.class);
    }
}