package com.example.lostandfound.service.state;

import com.example.lostandfound.domain.enums.ReportStatus;
import org.springframework.stereotype.Component;


@Component
public class RejectedState implements ReportState {

    @Override
    public ReportStatus getStatus() {
        return ReportStatus.REJECTED;
    }

    @Override
    public boolean canTransitionTo(ReportStatus target) {
        return false; // terminal state (แอดมินปฏิเสธประกาศแล้ว)
    }
}