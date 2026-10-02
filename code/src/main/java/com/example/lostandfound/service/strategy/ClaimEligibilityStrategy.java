package com.example.lostandfound.service.strategy;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.domain.enums.ReportType;

/**
 * GoF Strategy Pattern: กติกาตรวจสอบความน่าเชื่อถือของการยื่นเคลม
 * แตกต่างกันตามประเภทประกาศ (LOST/FOUND) — สลับ/เพิ่มกติกาใหม่ได้โดยไม่แก้ ClaimServiceImpl (Open/Closed)
 */
public interface ClaimEligibilityStrategy {

    ReportType supports();

    /** โยน BadRequestException ถ้าไม่ผ่านเกณฑ์ */
    void validate(Report report, User claimant, SubmitClaimRequest request);
}