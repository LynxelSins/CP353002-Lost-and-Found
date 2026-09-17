package com.example.lostandfound.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitClaimRequest {

    @NotBlank(message = "ต้องแนบหลักฐานยืนยันความเป็นเจ้าของ")
    private String evidenceText;

    private String evidenceImageUrl;
}