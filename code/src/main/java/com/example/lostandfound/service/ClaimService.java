package com.example.lostandfound.service;

import com.example.lostandfound.dto.request.ApproveClaimRequest;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.dto.response.ClaimResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// เพิ่ม import page, pageable
import java.util.List;
import java.util.UUID;

public interface ClaimService {

    ClaimResponse submit(UUID reportId, UUID claimantId, SubmitClaimRequest request);

    List<ClaimResponse> getByReport(UUID reportId, UUID requesterId);

    Page<ClaimResponse> getMyClaims(UUID claimantId, Pageable pageable);

    ClaimResponse approve(UUID claimId, UUID ownerId, ApproveClaimRequest request);

    ClaimResponse reject(UUID claimId, UUID ownerId);

    
}