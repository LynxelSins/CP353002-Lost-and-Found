package com.example.lostandfound.dto.response;

import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
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
public class ReportSummaryResponse {
    private UUID id;
    private ReportType type;
    private String title;
    private String locationName;
    private ReportStatus status;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
}