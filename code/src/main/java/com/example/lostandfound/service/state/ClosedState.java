package com.example.lostandfound.service.state;

import com.example.lostandfound.domain.enums.ReportStatus;
import org.springframework.stereotype.Component;

@Component
public class ClosedState implements ReportState {

    @Override
    public ReportStatus getStatus() {
        return ReportStatus.CLOSED;
    }

    @Override
    public boolean canTransitionTo(ReportStatus target) {
        return false; // สถานะสุดท้าย (terminal state) — ปิดเคสแล้วเปลี่ยนต่อไม่ได้
    }
}