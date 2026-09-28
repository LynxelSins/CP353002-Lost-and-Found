package com.example.lostandfound.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "กรุณากรอกชื่อ-นามสกุล")
    @Size(max = 150)
    private String fullName;

    @Size(max = 20)
    private String phoneNumber;
}