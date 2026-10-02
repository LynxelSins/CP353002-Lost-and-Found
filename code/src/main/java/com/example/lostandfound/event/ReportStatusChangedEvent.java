package com.example.lostandfound.event;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.enums.ReportStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReportStatusChangedEvent extends ApplicationEvent {

    private final Report report;
    private final ReportStatus oldStatus;
    private final ReportStatus newStatus;

    public ReportStatusChangedEvent(Object source, Report report, ReportStatus oldStatus, ReportStatus newStatus) {
        super(source);
        this.report = report;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}