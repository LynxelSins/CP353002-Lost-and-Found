package com.example.lostandfound.event;

import com.example.lostandfound.domain.entity.ReportWatcher;
import com.example.lostandfound.repository.ReportWatcherRepository;
import com.example.lostandfound.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observer: ทุกครั้งที่สถานะ Report เปลี่ยน จะแจ้งเตือนทุกคนใน report_watchers โดยอัตโนมัติ
 * (ขั้นที่ 3 ของเอกสาร Flow: "ทุกครั้งที่สถานะเปลี่ยนแปลง ระบบจะส่งการแจ้งเตือนไปยังผู้ใช้ทุกคนที่อยู่ใน report_watchers")
 */
@Component
@RequiredArgsConstructor
public class ReportStatusEventListener {

    private final ReportWatcherRepository reportWatcherRepository;
    private final NotificationService notificationService;

    @EventListener
    public void onReportStatusChanged(ReportStatusChangedEvent event) {
        String message = String.format("ประกาศ '%s' เปลี่ยนสถานะจาก %s เป็น %s",
                event.getReport().getTitle(), event.getOldStatus(), event.getNewStatus());

        for (ReportWatcher watcher : reportWatcherRepository.findByReportId(event.getReport().getId())) {
            notificationService.notify(watcher.getUser(), message);
        }
    }
}