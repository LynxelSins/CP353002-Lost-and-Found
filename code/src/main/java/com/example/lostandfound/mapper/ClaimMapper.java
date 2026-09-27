package com.example.lostandfound.mapper;

import com.example.lostandfound.domain.entity.Claim;
import com.example.lostandfound.dto.response.ClaimResponse;
import org.springframework.stereotype.Component;

@Component
public class ClaimMapper {

    public ClaimResponse toResponse(Claim claim) {
        return ClaimResponse.builder()
                .id(claim.getId())
                .reportId(claim.getReport().getId())
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