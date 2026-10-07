package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.Claim;
import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ClaimStatus;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ForbiddenException;
import com.example.lostandfound.mapper.ClaimMapper;
import com.example.lostandfound.repository.ClaimRepository;
import com.example.lostandfound.repository.ReportRepository;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.service.NotificationService;
import com.example.lostandfound.service.ReportStatusChanger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock private ClaimRepository claimRepository;
    @Mock private ReportRepository reportRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private ReportStatusChanger reportStatusChanger;
    @Mock private ClaimMapper claimMapper;
    @Mock private com.example.lostandfound.service.strategy.ClaimEligibilityStrategyResolver claimEligibilityStrategyResolver;
    @Mock private com.example.lostandfound.service.strategy.ClaimEligibilityStrategy claimEligibilityStrategy;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private UUID reportId;
    private UUID ownerId;
    private UUID claimantId;
    private Report report;
    private User owner;
    private User claimant;

    @BeforeEach
    void setUp() {
        reportId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        claimantId = UUID.randomUUID();

        owner = User.builder().id(ownerId).email("owner@test.com").build();
        claimant = User.builder().id(claimantId).email("claimant@test.com").build();

        report = Report.builder()
                .id(reportId)
                .user(owner)
                .title("กระเป๋าสตางค์สีน้ำตาลหาย")
                .status(ReportStatus.OPEN)
                .build();

       lenient().when(claimEligibilityStrategyResolver.resolve(any())).thenReturn(claimEligibilityStrategy);
    }

    // ---------- submit() ----------

    @Test
    void submit_shouldChangeReportToMatchPending_whenFirstClaimOnOpenReport() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userRepository.findById(claimantId)).thenReturn(Optional.of(claimant));
        when(claimRepository.existsByReportIdAndClaimantIdAndClaimStatus(reportId, claimantId, ClaimStatus.PENDING))
                .thenReturn(false);
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        SubmitClaimRequest request = new SubmitClaimRequest();
        request.setEvidenceText("จำได้ว่ามีรูปแมวข้างใน");

        claimService.submit(reportId, claimantId, request);

        verify(reportStatusChanger).changeStatus(report, ReportStatus.MATCH_PENDING, claimantId);
        verify(notificationService).notify(eq(owner), anyString());
    }

    @Test
    void submit_shouldNotChangeStatusAgain_whenReportAlreadyMatchPending() {
        report.setStatus(ReportStatus.MATCH_PENDING);
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userRepository.findById(claimantId)).thenReturn(Optional.of(claimant));
        when(claimRepository.existsByReportIdAndClaimantIdAndClaimStatus(reportId, claimantId, ClaimStatus.PENDING))
                .thenReturn(false);
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        claimService.submit(reportId, claimantId, new SubmitClaimRequest());

        verify(reportStatusChanger, never()).changeStatus(any(), any(), any());
    }

    @Test
    void submit_shouldThrow_whenReportIsClosed() {
        report.setStatus(ReportStatus.CLOSED);
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userRepository.findById(claimantId)).thenReturn(Optional.of(claimant));

        assertThatThrownBy(() -> claimService.submit(reportId, claimantId, new SubmitClaimRequest()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void submit_shouldThrow_whenClaimingOwnReport() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> claimService.submit(reportId, ownerId, new SubmitClaimRequest()))
                .isInstanceOf(BadRequestException.class);
    }

       @Test
    void submit_shouldThrow_whenDuplicatePendingClaimExists() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userRepository.findById(claimantId)).thenReturn(Optional.of(claimant));
        when(claimRepository.existsByReportIdAndClaimantIdAndClaimStatus(reportId, claimantId, ClaimStatus.PENDING))
                .thenReturn(true);

        assertThatThrownBy(() -> claimService.submit(reportId, claimantId, new SubmitClaimRequest()))
                .isInstanceOf(com.example.lostandfound.exception.ConflictException.class);
    }

    // ---------- approve() ----------

    @Test
    void approve_shouldRejectOtherPendingClaims_andSetReportClaimed() {
        UUID claimId = UUID.randomUUID();
        Claim targetClaim = Claim.builder()
                .id(claimId).report(report).claimant(claimant).claimStatus(ClaimStatus.PENDING)
                .build();

        User otherClaimant = User.builder().id(UUID.randomUUID()).email("other@test.com").build();
        Claim otherClaim = Claim.builder()
                .id(UUID.randomUUID()).report(report).claimant(otherClaimant).claimStatus(ClaimStatus.PENDING)
                .build();

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(targetClaim));
        when(claimRepository.findByReportIdAndClaimStatus(reportId, ClaimStatus.PENDING))
                .thenReturn(List.of(otherClaim));

        claimService.approve(claimId, ownerId, null);

        assertThat(targetClaim.getClaimStatus()).isEqualTo(ClaimStatus.APPROVED);
        assertThat(otherClaim.getClaimStatus()).isEqualTo(ClaimStatus.REJECTED);
        verify(reportStatusChanger).changeStatus(report, ReportStatus.CLAIMED, ownerId);
        verify(notificationService).notify(eq(otherClaimant), anyString());
        verify(notificationService).notify(eq(claimant), anyString());
    }

    @Test
    void approve_shouldThrow_whenRequesterIsNotOwner() {
        UUID claimId = UUID.randomUUID();
        UUID strangerId = UUID.randomUUID();
        Claim claim = Claim.builder().id(claimId).report(report).claimant(claimant).claimStatus(ClaimStatus.PENDING).build();
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        assertThatThrownBy(() -> claimService.approve(claimId, strangerId, null))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void approve_shouldThrow_whenClaimAlreadyDecided() {
        UUID claimId = UUID.randomUUID();
        Claim claim = Claim.builder().id(claimId).report(report).claimant(claimant).claimStatus(ClaimStatus.REJECTED).build();
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        assertThatThrownBy(() -> claimService.approve(claimId, ownerId, null))
                .isInstanceOf(BadRequestException.class);
    }

    // ---------- reject() ----------

    @Test
    void reject_shouldReopenReport_whenNoClaimsRemainPendingOrApproved() {
        report.setStatus(ReportStatus.MATCH_PENDING);
        UUID claimId = UUID.randomUUID();
        Claim claim = Claim.builder().id(claimId).report(report).claimant(claimant).claimStatus(ClaimStatus.PENDING).build();

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(claimRepository.countByReportIdAndClaimStatusIn(reportId, List.of(ClaimStatus.PENDING, ClaimStatus.APPROVED)))
                .thenReturn(0L);

        claimService.reject(claimId, ownerId);

        assertThat(claim.getClaimStatus()).isEqualTo(ClaimStatus.REJECTED);
        verify(reportStatusChanger).changeStatus(report, ReportStatus.OPEN, ownerId);
    }

    @Test
    void reject_shouldKeepMatchPending_whenOtherClaimsStillPendingOrApproved() {
        report.setStatus(ReportStatus.MATCH_PENDING);
        UUID claimId = UUID.randomUUID();
        Claim claim = Claim.builder().id(claimId).report(report).claimant(claimant).claimStatus(ClaimStatus.PENDING).build();

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(claimRepository.countByReportIdAndClaimStatusIn(reportId, List.of(ClaimStatus.PENDING, ClaimStatus.APPROVED)))
                .thenReturn(1L);

        claimService.reject(claimId, ownerId);

        verify(reportStatusChanger, never()).changeStatus(any(), any(), any());
    }

    @Test
    void reject_shouldThrow_whenRequesterIsNotOwner() {
        UUID claimId = UUID.randomUUID();
        UUID strangerId = UUID.randomUUID();
        Claim claim = Claim.builder().id(claimId).report(report).claimant(claimant).claimStatus(ClaimStatus.PENDING).build();
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        assertThatThrownBy(() -> claimService.reject(claimId, strangerId))
                .isInstanceOf(ForbiddenException.class);
    }
}