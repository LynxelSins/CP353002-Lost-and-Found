package com.example.lostandfound.dto.response;

import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private UUID id;
    private ReportType type;
    private String title;
    private String description;
    private String categoryName;
    private String locationName;
    private LocalDateTime eventTimestamp;
    private ReportStatus status;
    private List<String> tags;
    private List<ReportImageResponse> images;
    private UUID ownerId;
    private String ownerName;
    private long watcherCount;
    private long pendingClaimCount;
    private LocalDateTime createdAt;
}