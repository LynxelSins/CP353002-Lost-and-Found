package com.example.lostandfound.controller.api;

import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * จุดแทรกแซงของแอดมิน (ตามเอกสาร Flow): ลบโพสต์สแปม/ผิดกฎ พร้อม Cascade ทันที
 * จำกัดสิทธิ์เฉพาะ role STAFF เท่านั้น
 */
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STAFF')")
public class AdminReportController {

    private final ReportService reportService;

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        reportService.adminDelete(id);
        return ApiResponse.success("ลบประกาศ (พร้อมข้อมูลที่เกี่ยวข้องทั้งหมด) เรียบร้อย", null);
    }
}