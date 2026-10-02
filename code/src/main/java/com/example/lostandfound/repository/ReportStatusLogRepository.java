package com.example.lostandfound.repository;

import com.example.lostandfound.domain.entity.ReportStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportStatusLogRepository extends JpaRepository<ReportStatusLog, Long> {
    List<ReportStatusLog> findByReportIdOrderByCreatedAtAsc(UUID reportId);
}