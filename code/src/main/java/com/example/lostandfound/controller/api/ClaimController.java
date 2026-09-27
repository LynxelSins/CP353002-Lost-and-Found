package com.example.lostandfound.controller.api;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.example.lostandfound.dto.request.ApproveClaimRequest;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.dto.response.ClaimResponse;
import com.example.lostandfound.security.CurrentUser;
import com.example.lostandfound.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping("/api/reports/{reportId}/claims")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ClaimResponse> submit(@PathVariable UUID reportId,
                                              @CurrentUser UUID userId,
                                              @Valid @RequestBody SubmitClaimRequest request) {
        return ApiResponse.created(claimService.submit(reportId, userId, request));
    }

    @GetMapping("/api/reports/{reportId}/claims")
    public ApiResponse<List<ClaimResponse>> listForReport(@PathVariable UUID reportId,
                                                           @CurrentUser UUID userId) {
        return ApiResponse.success(claimService.getByReport(reportId, userId));
    }

    @PatchMapping("/api/claims/{claimId}/approve")
    public ApiResponse<ClaimResponse> approve(@PathVariable UUID claimId,
                                               @CurrentUser UUID userId,
                                               @RequestBody(required = false) ApproveClaimRequest request) {
        return ApiResponse.success(claimService.approve(claimId, userId, request));
    }

    @PatchMapping("/api/claims/{claimId}/reject")
    public ApiResponse<ClaimResponse> reject(@PathVariable UUID claimId, @CurrentUser UUID userId) {
        return ApiResponse.success(claimService.reject(claimId, userId));
    }

        @GetMapping("/api/claims/mine")
    public ApiResponse<Page<ClaimResponse>> mine(
            @CurrentUser UUID userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ApiResponse.success(claimService.getMyClaims(userId, pageable));
    }
}