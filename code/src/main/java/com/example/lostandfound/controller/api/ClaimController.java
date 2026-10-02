package com.example.lostandfound.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.lostandfound.dto.request.ApproveClaimRequest;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.dto.response.ClaimResponse;
import com.example.lostandfound.security.CurrentUser;
import com.example.lostandfound.service.ClaimService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping("/api/v1/reports/{reportId}/claims")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ClaimResponse> submit(@PathVariable UUID reportId,
            @CurrentUser UUID userId,
            @Valid @RequestBody SubmitClaimRequest request) {
        return ApiResponse.created(claimService.submit(reportId, userId, request));
    }

    @GetMapping("/api/v1/reports/{reportId}/claims")
    public ApiResponse<List<ClaimResponse>> listForReport(@PathVariable UUID reportId,
            @CurrentUser UUID userId) {
        return ApiResponse.success(claimService.getByReport(reportId, userId));
    }

    @PatchMapping("/api/v1/claims/{claimId}/approve")
    public ApiResponse<ClaimResponse> approve(@PathVariable UUID claimId,
            @CurrentUser UUID userId,
            @RequestBody(required = false) ApproveClaimRequest request) {
        return ApiResponse.success(claimService.approve(claimId, userId, request));
    }

    @PatchMapping("/api/v1/claims/{claimId}/reject")
    public ApiResponse<ClaimResponse> reject(@PathVariable UUID claimId, @CurrentUser UUID userId) {
        return ApiResponse.success(claimService.reject(claimId, userId));
    }

    @GetMapping("/api/v1/claims/mine")
    public ApiResponse<Page<ClaimResponse>> mine(
            @CurrentUser UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(claimService.getMyClaims(userId, pageable));
    }
}
