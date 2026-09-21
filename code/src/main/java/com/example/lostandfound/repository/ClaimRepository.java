package com.example.lostandfound.repository;

import com.example.lostandfound.domain.entity.Claim;
import com.example.lostandfound.domain.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findByReportIdOrderByCreatedAtAsc(UUID reportId);
    List<Claim> findByReportIdAndClaimStatus(UUID reportId, ClaimStatus status);
    boolean existsByReportIdAndClaimantIdAndClaimStatus(UUID reportId, UUID claimantId, ClaimStatus status);
    long countByReportIdAndClaimStatusIn(UUID reportId, List<ClaimStatus> statuses);
}