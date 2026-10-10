package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.Tag;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
import com.example.lostandfound.dto.request.CreateReportRequest;
import com.example.lostandfound.dto.response.ReportResponse;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ForbiddenException;
import com.example.lostandfound.mapper.ReportMapper;
import com.example.lostandfound.repository.*;
import com.example.lostandfound.service.ReportStatusChanger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock private ReportRepository reportRepository;
    @Mock private ReportImageRepository reportImageRepository;
    @Mock private ReportStatusLogRepository reportStatusLogRepository;
    @Mock private ReportWatcherRepository reportWatcherRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TagRepository tagRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReportMapper reportMapper;
    @Mock private ReportStatusChanger reportStatusChanger;

    @InjectMocks
    private ReportServiceImpl reportService;

    private UUID ownerId;
    private User owner;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        owner = User.builder().id(ownerId).email("a@test.com").build();
    }

    @Test
    void create_shouldReuseExistingTag_andCreateNewTagOnlyWhenMissing() {
        CreateReportRequest request = new CreateReportRequest();
        request.setType(ReportType.LOST);
        request.setTitle("ทำกระเป๋าสตางค์สีน้ำตาลหาย");
        request.setLocationName("โรงอาหาร ตึก B");
        request.setEventTimestamp(LocalDateTime.now());
        request.setTagNames(List.of("กระเป๋าสีน้ำตาล", "โรงอาหาร"));

        Tag existingTag = Tag.builder().id(5L).tagName("โรงอาหาร").build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(tagRepository.findByTagNameIgnoreCase("กระเป๋าสีน้ำตาล")).thenReturn(Optional.empty());
        when(tagRepository.findByTagNameIgnoreCase("โรงอาหาร")).thenReturn(Optional.of(existingTag));
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> {
            Tag t = inv.getArgument(0);
            t.setId(20L);
            return t;
        });
        when(reportRepository.save(any(Report.class))).thenAnswer(inv -> {
            Report r = inv.getArgument(0);
            if (r.getId() == null) r.setId(UUID.randomUUID());
            return r;
        });
        when(reportRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(Report.builder().id(UUID.randomUUID()).build()));
        when(reportMapper.toResponse(any(Report.class))).thenReturn(ReportResponse.builder().build());

        reportService.create(ownerId, request);

        // แท็กใหม่ถูกสร้างแค่ตัวเดียว ("กระเป๋าสีน้ำตาล") ส่วน "โรงอาหาร" ใช้ตัวเดิม ไม่สร้างซ้ำ
        verify(tagRepository, times(1)).save(any(Tag.class));
        verify(reportStatusLogRepository).save(argThat(log ->
                log.getOldStatus() == null && "OPEN".equals(log.getNewStatus())));
    }

    @Test
    void closeReport_shouldThrow_whenRequesterIsNotOwner() {
        UUID reportId = UUID.randomUUID();
        UUID strangerId = UUID.randomUUID();
        Report report = Report.builder().id(reportId).user(owner).status(ReportStatus.CLAIMED).build();
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        assertThatThrownBy(() -> reportService.closeReport(reportId, strangerId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void closeReport_shouldThrow_whenReportNotClaimedYet() {
        UUID reportId = UUID.randomUUID();
        Report report = Report.builder().id(reportId).user(owner).status(ReportStatus.MATCH_PENDING).build();
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        assertThatThrownBy(() -> reportService.closeReport(reportId, ownerId))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void closeReport_shouldSucceed_whenOwnerAndStatusClaimed() {
        UUID reportId = UUID.randomUUID();
        Report report = Report.builder().id(reportId).user(owner).status(ReportStatus.CLAIMED).build();
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        reportService.closeReport(reportId, ownerId);

        verify(reportStatusChanger).changeStatus(report, ReportStatus.CLOSED, ownerId);
    }
}