package com.example.lostandfound.repository;

import com.example.lostandfound.domain.entity.ReportWatcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportWatcherRepository extends JpaRepository<ReportWatcher, Long> {
    boolean existsByReportIdAndUserId(UUID reportId, UUID userId);
    List<ReportWatcher> findByReportId(UUID reportId);
    void deleteByReportIdAndUserId(UUID reportId, UUID userId);
}