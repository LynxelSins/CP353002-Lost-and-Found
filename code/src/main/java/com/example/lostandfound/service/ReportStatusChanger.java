package com.example.lostandfound.service;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.ReportStatusLog;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.event.ReportStatusChangedEvent;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.repository.ReportRepository;
import com.example.lostandfound.repository.ReportStatusLogRepository;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.service.state.ReportState;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * รวมตรรกะการเปลี่ยนสถานะ Report ไว้ที่เดียว (Single Responsibility)
 * ใช้ GoF State Pattern จริง: ตรวจ transition ผ่าน ReportState ของแต่ละสถานะ
 * แทนการเช็ค if-else สถานะแบบเดิม (Open/Closed — เพิ่มสถานะใหม่แค่เพิ่ม ReportState class)
 */
@Service
@RequiredArgsConstructor
public class ReportStatusChanger {

    private final ReportRepository reportRepository;
    private final ReportStatusLogRepository reportStatusLogRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final List<ReportState> states;

    private Map<ReportStatus, ReportState> stateMap;

    @PostConstruct
    void init() {
        stateMap = states.stream()
                .collect(Collectors.toMap(ReportState::getStatus, s -> s));
    }

    @Transactional
    public void changeStatus(Report report, ReportStatus newStatus, UUID actorId) {
        ReportStatus oldStatus = report.getStatus();
        if (oldStatus == newStatus) {
            return;
        }

        ReportState currentState = stateMap.get(oldStatus);
        if (currentState == null || !currentState.canTransitionTo(newStatus)) {
            throw new ConflictException(
                    "ไม่สามารถเปลี่ยนสถานะจาก " + oldStatus + " เป็น " + newStatus + " ได้ (ผิด workflow)");
        }

        report.setStatus(newStatus);
        reportRepository.save(report);

        User actor = actorId != null ? userRepository.findById(actorId).orElse(null) : null;
        reportStatusLogRepository.save(ReportStatusLog.builder()
                .report(report)
                .changedBy(actor)
                .oldStatus(oldStatus.name())
                .newStatus(newStatus.name())
                .build());

        eventPublisher.publishEvent(new ReportStatusChangedEvent(this, report, oldStatus, newStatus));
    }
}