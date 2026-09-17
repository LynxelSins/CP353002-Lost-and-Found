package com.example.lostandfound.service;

import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
import com.example.lostandfound.dto.request.CreateReportRequest;
import com.example.lostandfound.dto.response.ReportResponse;
import com.example.lostandfound.dto.response.ReportSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReportService {

    ReportResponse create(UUID ownerId, CreateReportRequest request);

    ReportResponse getById(UUID reportId);

    Page<ReportSummaryResponse> search(ReportType type, ReportStatus status, Long categoryId,
                                        String tagName, String keyword, Pageable pageable);

    void watch(UUID reportId, UUID userId);

    void unwatch(UUID reportId, UUID userId);

    void closeReport(UUID reportId, UUID ownerId);

    void adminDelete(UUID reportId);
}