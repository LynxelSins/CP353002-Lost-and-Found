package com.example.lostandfound.service.strategy;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ReportType;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * ประกาศประเภท LOST (เจ้าของทำของหาย) -> คนยื่นเคลมคือ "ผู้ที่บอกว่าเจอของ"
 * ต้องอธิบายหลักฐานเป็นข้อความอย่างน้อย เพื่อให้เจ้าของตรวจสอบได้ว่าเจอจริง
 */
@Component
public class LostReportClaimEligibilityStrategy implements ClaimEligibilityStrategy {

    @Override
    public ReportType supports() {
        return ReportType.LOST;
    }

    @Override
    public void validate(Report report, User claimant, SubmitClaimRequest request) {
        if (!StringUtils.hasText(request.getEvidenceText()) || request.getEvidenceText().trim().length() < 10) {
            throw new BadRequestException(
                    "กรุณาอธิบายหลักฐานว่าเจอสิ่งของนี้อย่างไร (อย่างน้อย 10 ตัวอักษร) ก่อนยื่นเคลมประกาศของหาย");
        }
    }
}