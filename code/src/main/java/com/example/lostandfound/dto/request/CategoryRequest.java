package com.example.lostandfound.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "ชื่อหมวดหมู่ห้ามว่าง")
    @Size(max = 100, message = "ชื่อหมวดหมู่ยาวเกิน 100 ตัวอักษร")
    private String categoryName;

    @Size(max = 500, message = "คำอธิบายยาวเกิน 500 ตัวอักษร")
    private String description;
}