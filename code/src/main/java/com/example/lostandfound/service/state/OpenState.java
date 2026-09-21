package com.example.lostandfound.service.state;

import com.example.lostandfound.domain.enums.ReportStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OpenState implements ReportState {

    private static final Set<ReportStatus> ALLOWED =
            Set.of(ReportStatus.MATCH_PENDING, ReportStatus.REJECTED);

    @Override
    public ReportStatus getStatus() {
        return ReportStatus.OPEN;
    }

    @Override
    public boolean canTransitionTo(ReportStatus target) {
        return ALLOWED.contains(target);
    }
}