package com.example.lostandfound.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "กรุณากรอกอีเมล")
    @Email(message = "รูปแบบอีเมลไม่ถูกต้อง")
    private String email;

    @NotBlank(message = "กรุณากรอกรหัสผ่าน")
    @Size(min = 8, message = "รหัสผ่านต้องมีอย่างน้อย 8 ตัวอักษร")
    private String password;

    @NotBlank(message = "กรุณากรอกชื่อ-นามสกุล")
    private String fullName;
}