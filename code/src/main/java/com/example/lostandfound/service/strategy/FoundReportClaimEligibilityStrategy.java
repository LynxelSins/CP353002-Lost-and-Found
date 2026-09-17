package com.example.lostandfound.service.strategy;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ReportType;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * ประกาศประเภท FOUND (มีคนเก็บของได้) -> คนยื่นเคลมคือ "เจ้าของของจริง"
 * ต้องมีหลักฐานยืนยันความเป็นเจ้าของ (ข้อความหรือรูป) อย่างน้อย 1 อย่าง
 */
@Component
public class FoundReportClaimEligibilityStrategy implements ClaimEligibilityStrategy {

    @Override
    public ReportType supports() {
        return ReportType.FOUND;
    }

    @Override
    public void validate(Report report, User claimant, SubmitClaimRequest request) {
        boolean hasText = StringUtils.hasText(request.getEvidenceText());
        boolean hasImage = StringUtils.hasText(request.getEvidenceImageUrl());
        if (!hasText && !hasImage) {
            throw new BadRequestException(
                    "กรุณาแนบหลักฐานยืนยันความเป็นเจ้าของ (ข้อความอธิบายหรือรูปภาพ) ก่อนยื่นเคลมประกาศของที่เก็บได้");
        }
    }
}