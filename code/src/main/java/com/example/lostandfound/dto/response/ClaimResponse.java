package com.example.lostandfound.dto.response;

import com.example.lostandfound.domain.enums.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {
    private UUID id;
    private UUID reportId;
    private UUID claimantId;
    private String claimantName;
    private String evidenceText;
    private String evidenceImageUrl;
    private ClaimStatus claimStatus;
    private String meetingLocation;
    private LocalDateTime meetingTime;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
}