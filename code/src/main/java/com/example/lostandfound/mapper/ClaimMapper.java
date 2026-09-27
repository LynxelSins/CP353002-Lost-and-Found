package com.example.lostandfound.mapper;

import com.example.lostandfound.domain.entity.Claim;
import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.dto.response.ClaimResponse;
import org.springframework.stereotype.Component;

@Component
public class ClaimMapper {

    public ClaimResponse toResponse(Claim claim) {
        Report report = claim.getReport();
        String thumbnail = report.getImages().isEmpty() ? null : report.getImages().get(0).getImageUrl();

        return ClaimResponse.builder()
                .id(claim.getId())
                .reportId(report.getId())
                .reportTitle(report.getTitle())
                .reportLocationName(report.getLocationName())
                .reportStatus(report.getStatus())
                .reportThumbnailUrl(thumbnail)
                .claimantId(claim.getClaimant().getId())
                .claimantName(claim.getClaimant().getProfile() != null
                        ? claim.getClaimant().getProfile().getFullName()
                        : claim.getClaimant().getEmail())
                .evidenceText(claim.getEvidenceText())
                .evidenceImageUrl(claim.getEvidenceImageUrl())
                .claimStatus(claim.getClaimStatus())
                .meetingLocation(claim.getMeetingLocation())
                .meetingTime(claim.getMeetingTime())
                .resolvedAt(claim.getResolvedAt())
                .createdAt(claim.getCreatedAt())
                .build();
    }
}