package com.example.lostandfound.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 150)
    private String fullName;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 500)
    private String avatarUrl;
}