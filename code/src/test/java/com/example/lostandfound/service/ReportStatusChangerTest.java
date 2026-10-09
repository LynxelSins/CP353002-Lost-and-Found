package com.example.lostandfound.service;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.repository.ReportRepository;
import com.example.lostandfound.repository.ReportStatusLogRepository;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.service.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportStatusChangerTest {

    @Mock private ReportRepository reportRepository;
    @Mock private ReportStatusLogRepository reportStatusLogRepository;
    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private ReportStatusChanger reportStatusChanger;

    @BeforeEach
    void setUp() {
        List<com.example.lostandfound.service.state.ReportState> states = List.of(
                new OpenState(), new MatchPendingState(), new ClaimedState(),
                new ClosedState(), new RejectedState());
        reportStatusChanger = new ReportStatusChanger(
                reportRepository, reportStatusLogRepository, userRepository, eventPublisher, states);
        reportStatusChanger.init(); // เรียก @PostConstruct เองในเทส (ไม่มี Spring context)
    }

    @Test
    void changeStatus_shouldSucceed_onValidTransition() {
        Report report = Report.builder().id(UUID.randomUUID()).status(ReportStatus.OPEN).build();

        reportStatusChanger.changeStatus(report, ReportStatus.MATCH_PENDING, null);

        assertThat(report.getStatus()).isEqualTo(ReportStatus.MATCH_PENDING);
        verify(reportRepository).save(report);
        verify(reportStatusLogRepository).save(any());
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void changeStatus_shouldThrowConflict_onInvalidTransition() {
        // CLAIMED เปลี่ยนกลับเป็น OPEN ตรงๆ ไม่ได้ตาม workflow
        Report report = Report.builder().id(UUID.randomUUID()).status(ReportStatus.CLAIMED).build();

        assertThatThrownBy(() -> reportStatusChanger.changeStatus(report, ReportStatus.OPEN, null))
                .isInstanceOf(ConflictException.class);

        verify(reportRepository, never()).save(any());
    }

    @Test
    void changeStatus_shouldDoNothing_whenSameStatus() {
        Report report = Report.builder().id(UUID.randomUUID()).status(ReportStatus.OPEN).build();

        reportStatusChanger.changeStatus(report, ReportStatus.OPEN, null);

        verify(reportRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void changeStatus_shouldThrowConflict_fromTerminalClosedState() {
        Report report = Report.builder().id(UUID.randomUUID()).status(ReportStatus.CLOSED).build();

        assertThatThrownBy(() -> reportStatusChanger.changeStatus(report, ReportStatus.OPEN, null))
                .isInstanceOf(ConflictException.class);
    }
}