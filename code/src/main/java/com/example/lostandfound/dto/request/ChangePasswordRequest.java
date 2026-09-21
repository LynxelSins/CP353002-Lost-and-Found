package com.example.lostandfound.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    // ไม่บังคับ @NotBlank เพราะบัญชีที่ล็อกอินด้วย Google ล้วนๆ (ยังไม่มีรหัสผ่านเดิม) จะไม่มีค่านี้
    private String currentPassword;

    @NotBlank(message = "กรุณากรอกรหัสผ่านใหม่")
    @Size(min = 8, message = "รหัสผ่านใหม่ต้องมีอย่างน้อย 8 ตัวอักษร")
    private String newPassword;
}