package com.example.lostandfound.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagRequest {

    @NotBlank(message = "ชื่อแท็กห้ามว่าง")
    @Size(max = 100, message = "ชื่อแท็กยาวเกิน 100 ตัวอักษร")
    private String tagName;
}