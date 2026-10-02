package com.example.lostandfound.service.state;

import com.example.lostandfound.domain.enums.ReportStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MatchPendingState implements ReportState {

    // OPEN: ปฏิเสธ claim ที่เหลือหมด -> เปิดกลับ (ตาม Flow เอกสาร)
    // CLAIMED: มีคน approve
    private static final Set<ReportStatus> ALLOWED =
            Set.of(ReportStatus.OPEN, ReportStatus.CLAIMED, ReportStatus.REJECTED);

    @Override
    public ReportStatus getStatus() {
        return ReportStatus.MATCH_PENDING;
    }

    @Override
    public boolean canTransitionTo(ReportStatus target) {
        return ALLOWED.contains(target);
    }
}