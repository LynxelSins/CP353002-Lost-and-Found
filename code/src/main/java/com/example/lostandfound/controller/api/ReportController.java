// controller/api/ReportController.java
package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
import com.example.lostandfound.dto.request.CreateReportRequest;
import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.dto.response.ReportResponse;
import com.example.lostandfound.dto.response.ReportSummaryResponse;
import com.example.lostandfound.security.CurrentUser;
import com.example.lostandfound.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReportResponse> create(@CurrentUser UUID userId,
                                               @Valid @RequestBody CreateReportRequest request) {
        return ApiResponse.created(reportService.create(userId, request));
    }

    // Pagination & Sorting endpoint เช่น GET /api/reports?tag=กระเป๋าสีน้ำตาล&page=0&size=10&sort=createdAt,desc
    @GetMapping
    public ApiResponse<Page<ReportSummaryResponse>> search(
            @RequestParam(required = false) ReportType type,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ApiResponse.success(reportService.search(type, status, categoryId, tag, keyword, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(reportService.getById(id));
    }

    @PostMapping("/{id}/watch")
    public ApiResponse<Void> watch(@PathVariable UUID id, @CurrentUser UUID userId) {
        reportService.watch(id, userId);
        return ApiResponse.success("ติดตามประกาศนี้แล้ว", null);
    }

    @DeleteMapping("/{id}/watch")
    public ApiResponse<Void> unwatch(@PathVariable UUID id, @CurrentUser UUID userId) {
        reportService.unwatch(id, userId);
        return ApiResponse.success("เลิกติดตามประกาศนี้แล้ว", null);
    }

    @PostMapping("/{id}/close")
    public ApiResponse<Void> close(@PathVariable UUID id, @CurrentUser UUID userId) {
        reportService.closeReport(id, userId);
        return ApiResponse.success("ปิดเคสเรียบร้อย", null);
    }
}