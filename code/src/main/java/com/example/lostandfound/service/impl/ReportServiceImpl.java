package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.Category;
import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.ReportImage;
import com.example.lostandfound.domain.entity.ReportStatusLog;
import com.example.lostandfound.domain.entity.ReportWatcher;
import com.example.lostandfound.domain.entity.Tag;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
import com.example.lostandfound.dto.request.CreateReportRequest;
import com.example.lostandfound.dto.response.ReportResponse;
import com.example.lostandfound.dto.response.ReportSummaryResponse;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.exception.UnauthorizedException;
import com.example.lostandfound.mapper.ReportMapper;
import com.example.lostandfound.repository.CategoryRepository;
import com.example.lostandfound.repository.ReportImageRepository;
import com.example.lostandfound.repository.ReportRepository;
import com.example.lostandfound.repository.ReportStatusLogRepository;
import com.example.lostandfound.repository.ReportWatcherRepository;
import com.example.lostandfound.repository.TagRepository;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.service.ReportService;
import com.example.lostandfound.service.ReportStatusChanger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;
    private final ReportStatusLogRepository reportStatusLogRepository;
    private final ReportWatcherRepository reportWatcherRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;
    private final ReportStatusChanger reportStatusChanger;

    @Override
    @Transactional
    public ReportResponse create(UUID ownerId, CreateReportRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }

        Report report = Report.builder()
                .user(owner)
                .category(category)
                .type(request.getType())
                .title(request.getTitle())
                .description(request.getDescription())
                .locationName(request.getLocationName())
                .eventTimestamp(request.getEventTimestamp())
                .status(ReportStatus.OPEN)
                .tags(resolveTags(request.getTagNames()))
                .build();

        Report saved = reportRepository.save(report);

        // ขั้นที่ 2 ของเอกสาร Flow: แนบรูปภาพ
        if (request.getImageUrls() != null) {
            for (String url : request.getImageUrls()) {
                reportImageRepository.save(ReportImage.builder()
                        .report(saved)
                        .imageUrl(url)
                        .build());
            }
        }

        // บันทึก Log แรก: NULL -> OPEN
        reportStatusLogRepository.save(ReportStatusLog.builder()
                .report(saved)
                .changedBy(owner)
                .oldStatus(null)
                .newStatus(ReportStatus.OPEN.name())
                .build());

        return reportMapper.toResponse(reportRepository.findById(saved.getId()).orElseThrow());
    }

    /**
     * ตรวจ Tag ทีละอัน: มีอยู่แล้ว (case-insensitive) -> ใช้ตัวเดิม, ยังไม่มี -> สร้างใหม่
     * ตรงตามพฤติกรรมที่เอกสาร Flow ระบุไว้ในขั้นที่ 2 ข้อ 3
     */
    private Set<Tag> resolveTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames == null) {
            return tags;
        }
        for (String rawName : tagNames) {
            String name = rawName.trim();
            if (name.isEmpty()) {
                continue;
            }
            Tag tag = tagRepository.findByTagNameIgnoreCase(name)
                    .orElseGet(() -> tagRepository.save(Tag.builder().tagName(name).build()));
            tags.add(tag);
        }
        return tags;
    }

    @Override
    public ReportResponse getById(UUID reportId) {
        return reportMapper.toResponse(findEntityById(reportId));
    }

    @Override
    public Page<ReportSummaryResponse> search(ReportType type, ReportStatus status, Long categoryId,
                                               String tagName, String keyword, Pageable pageable) {
        return reportRepository.search(type, status, categoryId, tagName, keyword, pageable)
                .map(reportMapper::toSummary);
    }

    @Override
    @Transactional
    public void watch(UUID reportId, UUID userId) {
        Report report = findEntityById(reportId);
        if (reportWatcherRepository.existsByReportIdAndUserId(reportId, userId)) {
            return; // กดซ้ำ -> ไม่ error ตาม UX ปกติของปุ่ม toggle
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        reportWatcherRepository.save(ReportWatcher.builder().report(report).user(user).build());
    }

    @Override
    @Transactional
    public void unwatch(UUID reportId, UUID userId) {
        reportWatcherRepository.deleteByReportIdAndUserId(reportId, userId);
    }

    @Override
    @Transactional
    public void closeReport(UUID reportId, UUID ownerId) {
        Report report = findEntityById(reportId);

        if (report.getUser() == null || !report.getUser().getId().equals(ownerId)) {
            throw new UnauthorizedException("เฉพาะเจ้าของประกาศเท่านั้นที่ปิดเคสได้");
        }
        if (report.getStatus() != ReportStatus.CLAIMED) {
            throw new BadRequestException("ปิดเคสได้เฉพาะประกาศที่อยู่ในสถานะ CLAIMED เท่านั้น");
        }

        reportStatusChanger.changeStatus(report, ReportStatus.CLOSED, ownerId);
    }

    @Override
    @Transactional
    public void adminDelete(UUID reportId) {
        // claims, logs, watchers, images ถูกลบตาม Cascade ที่ตั้งไว้ใน Entity + ON DELETE CASCADE ใน schema.sql
        // ตัว tags (master data) ไม่ถูกลบ — ลบแค่ความสัมพันธ์ใน report_tags ตรงตามเอกสาร Flow
        Report report = findEntityById(reportId);
        reportRepository.delete(report);
    }

    private Report findEntityById(UUID id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));
    }
}