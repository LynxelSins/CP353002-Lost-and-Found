package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.Claim;
import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ClaimStatus;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.dto.request.ApproveClaimRequest;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.dto.response.ClaimResponse;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.exception.UnauthorizedException;
import com.example.lostandfound.mapper.ClaimMapper;
import com.example.lostandfound.repository.ClaimRepository;
import com.example.lostandfound.repository.ReportRepository;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.service.ClaimService;
import com.example.lostandfound.service.NotificationService;
import com.example.lostandfound.service.ReportStatusChanger;
import com.example.lostandfound.service.strategy.ClaimEligibilityStrategy;
import com.example.lostandfound.service.strategy.ClaimEligibilityStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ReportStatusChanger reportStatusChanger;
    private final ClaimMapper claimMapper;
    private final ClaimEligibilityStrategyResolver claimEligibilityStrategyResolver;

    @Override
    @Transactional
    public ClaimResponse submit(UUID reportId, UUID claimantId, SubmitClaimRequest request) {
        Report report = findReport(reportId);
        User claimant = userRepository.findById(claimantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", claimantId));

        if (report.getStatus() != ReportStatus.OPEN && report.getStatus() != ReportStatus.MATCH_PENDING) {
            throw new BadRequestException("ประกาศนี้ไม่เปิดรับการเคลมแล้ว (สถานะปัจจุบัน: " + report.getStatus() + ")");
        }
        if (report.getUser() != null && report.getUser().getId().equals(claimantId)) {
            throw new BadRequestException("ไม่สามารถยื่นเคลมประกาศของตัวเองได้");
        }
        if (claimRepository.existsByReportIdAndClaimantIdAndClaimStatus(reportId, claimantId, ClaimStatus.PENDING)) {
            throw new ConflictException("คุณมีคำขอเคลมที่รอตรวจสอบอยู่กับประกาศนี้แล้ว");
        }

        ClaimEligibilityStrategy strategy = claimEligibilityStrategyResolver.resolve(report.getType());
        if (strategy != null) {
            strategy.validate(report, claimant, request);
        }

        Claim claim = claimRepository.save(Claim.builder()
                .report(report)
                .claimant(claimant)
                .evidenceText(request.getEvidenceText())
                .evidenceImageUrl(request.getEvidenceImageUrl())
                .claimStatus(ClaimStatus.PENDING)
                .build());

        // ขั้นที่ 4 ของเอกสาร Flow: Claim แรกเข้ามา -> OPEN เปลี่ยนเป็น MATCH_PENDING ทันที
        if (report.getStatus() == ReportStatus.OPEN) {
            reportStatusChanger.changeStatus(report, ReportStatus.MATCH_PENDING, claimantId);
        }

        if (report.getUser() != null) {
            notificationService.notify(report.getUser(),
                    "มีผู้ยื่นเคลมประกาศ '" + report.getTitle() + "' ของคุณใหม่ กรุณาตรวจสอบ");
        }

        return claimMapper.toResponse(claim);
    }

    @Override
    public List<ClaimResponse> getByReport(UUID reportId, UUID requesterId) {
        Report report = findReport(reportId);
        assertOwner(report, requesterId);
        return claimRepository.findByReportIdOrderByCreatedAtAsc(reportId).stream()
                .map(claimMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ClaimResponse approve(UUID claimId, UUID ownerId, ApproveClaimRequest request) {
        Claim claim = findClaim(claimId);
        Report report = claim.getReport();
        assertOwner(report, ownerId);

        if (claim.getClaimStatus() != ClaimStatus.PENDING) {
            throw new BadRequestException("คำขอเคลมนี้ถูกตัดสินใจไปแล้ว");
        }

        claim.setClaimStatus(ClaimStatus.APPROVED);
        claim.setResolvedAt(LocalDateTime.now());
        if (request != null) {
            claim.setMeetingLocation(request.getMeetingLocation());
            claim.setMeetingTime(request.getMeetingTime());
        }
        claimRepository.save(claim);

        // ขั้นที่ 5 ของเอกสาร Flow: อนุมัติ 1 ราย -> ปฏิเสธที่เหลือทั้งหมดโดยอัตโนมัติ
        for (Claim other : claimRepository.findByReportIdAndClaimStatus(report.getId(), ClaimStatus.PENDING)) {
            other.setClaimStatus(ClaimStatus.REJECTED);
            other.setResolvedAt(LocalDateTime.now());
            claimRepository.save(other);
            notificationService.notify(other.getClaimant(),
                    "คำขอเคลมของคุณสำหรับประกาศ '" + report.getTitle() + "' ถูกปฏิเสธ เนื่องจากมีผู้อื่นได้รับการอนุมัติแล้ว");
        }

        reportStatusChanger.changeStatus(report, ReportStatus.CLAIMED, ownerId);

        notificationService.notify(claim.getClaimant(),
                "คำขอเคลมของคุณสำหรับประกาศ '" + report.getTitle() + "' ได้รับการอนุมัติแล้ว");

        return claimMapper.toResponse(claim);
    }

    @Override
    @Transactional
    public ClaimResponse reject(UUID claimId, UUID ownerId) {
        Claim claim = findClaim(claimId);
        Report report = claim.getReport();
        assertOwner(report, ownerId);

        if (claim.getClaimStatus() != ClaimStatus.PENDING) {
            throw new BadRequestException("คำขอเคลมนี้ถูกตัดสินใจไปแล้ว");
        }

        claim.setClaimStatus(ClaimStatus.REJECTED);
        claim.setResolvedAt(LocalDateTime.now());
        claimRepository.save(claim);

        notificationService.notify(claim.getClaimant(),
                "คำขอเคลมของคุณสำหรับประกาศ '" + report.getTitle() + "' ถูกปฏิเสธ");

        // กติกาตามเอกสาร Flow: ถ้าไม่เหลือ PENDING/APPROVED เลย -> กลับเป็น OPEN อัตโนมัติ
        long remaining = claimRepository.countByReportIdAndClaimStatusIn(
                report.getId(), List.of(ClaimStatus.PENDING, ClaimStatus.APPROVED));
        if (remaining == 0 && report.getStatus() == ReportStatus.MATCH_PENDING) {
            reportStatusChanger.changeStatus(report, ReportStatus.OPEN, ownerId);
        }

        return claimMapper.toResponse(claim);
    }

    private void assertOwner(Report report, UUID userId) {
        if (report.getUser() == null || !report.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("เฉพาะเจ้าของประกาศเท่านั้นที่มีสิทธิ์ทำรายการนี้");
        }
    }

    private Report findReport(UUID id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));
    }

    private Claim findClaim(UUID id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", "id", id));
    }
}