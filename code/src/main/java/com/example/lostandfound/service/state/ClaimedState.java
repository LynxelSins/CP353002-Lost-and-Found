package com.example.lostandfound.service.state;

import com.example.lostandfound.domain.enums.ReportStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ClaimedState implements ReportState {

    private static final Set<ReportStatus> ALLOWED = Set.of(ReportStatus.CLOSED);

    @Override
    public ReportStatus getStatus() {
        return ReportStatus.CLAIMED;
    }

    @Override
    public boolean canTransitionTo(ReportStatus target) {
        return ALLOWED.contains(target);
    }
}