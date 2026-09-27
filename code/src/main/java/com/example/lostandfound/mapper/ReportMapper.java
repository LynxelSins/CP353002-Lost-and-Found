package com.example.lostandfound.mapper;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.Tag;
import com.example.lostandfound.domain.enums.ClaimStatus;
import com.example.lostandfound.dto.response.ReportImageResponse;
import com.example.lostandfound.dto.response.ReportResponse;
import com.example.lostandfound.dto.response.ReportSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponse toResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .type(report.getType())
                .title(report.getTitle())
                .description(report.getDescription())
                .categoryName(report.getCategory() != null ? report.getCategory().getCategoryName() : null)
                .locationName(report.getLocationName())
                .eventTimestamp(report.getEventTimestamp())
                .status(report.getStatus())
                .tags(report.getTags().stream().map(Tag::getTagName).toList())
                .images(report.getImages().stream()
                        .map(img -> ReportImageResponse.builder()
                                .id(img.getId())
                                .imageUrl(img.getImageUrl())
                                .build())
                        .toList())
                .ownerId(report.getUser() != null ? report.getUser().getId() : null)
                .ownerName(resolveOwnerName(report))
                .watcherCount(report.getWatchers().size())
                .pendingClaimCount(report.getClaims().stream()
                        .filter(c -> c.getClaimStatus() == ClaimStatus.PENDING)
                        .count())
                .createdAt(report.getCreatedAt())
                .build();
    }

    public ReportSummaryResponse toSummary(Report report) {
        String thumbnail = report.getImages().isEmpty() ? null : report.getImages().get(0).getImageUrl();
        return ReportSummaryResponse.builder()
                .id(report.getId())
                .type(report.getType())
                .title(report.getTitle())
                .locationName(report.getLocationName())
                .status(report.getStatus())
                .thumbnailUrl(thumbnail)
                .createdAt(report.getCreatedAt())
                .build();
    }

    private String resolveOwnerName(Report report) {
        if (report.getUser() == null) {
            return "ผู้ใช้ที่ถูกลบบัญชี";
        }
        return report.getUser().getProfile() != null
                ? report.getUser().getProfile().getFullName()
                : report.getUser().getEmail();
    }
}