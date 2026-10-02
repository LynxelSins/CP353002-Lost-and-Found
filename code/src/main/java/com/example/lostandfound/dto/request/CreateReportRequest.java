package com.example.lostandfound.dto.request;

import com.example.lostandfound.domain.enums.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateReportRequest {

    @NotNull(message = "ต้องระบุประเภทประกาศ (LOST/FOUND)")
    private ReportType type;

    @NotBlank(message = "title ห้ามว่าง")
    @Size(max = 200, message = "title ยาวเกิน 200 ตัวอักษร")
    private String title;

    private String description;

    private Long categoryId;

    @NotBlank(message = "ต้องระบุสถานที่")
    @Size(max = 255)
    private String locationName;

    @NotNull(message = "ต้องระบุวันเวลาที่เกิดเหตุ")
    private LocalDateTime eventTimestamp;

    /** URL รูปที่อัปโหลดไว้แล้วผ่าน POST /api/uploads — บังคับแนบอย่างน้อย 1 รูป (สูงสุด 5 รูป ตรงกับ frontend) */
    @NotEmpty(message = "ต้องแนบรูปภาพอย่างน้อย 1 รูป")
    @Size(max = 5, message = "แนบรูปได้สูงสุด 5 รูป")
    private List<@NotBlank String> imageUrls;

    /** ไม่บังคับ (Optional Field) — ตามที่เอกสาร Flow ระบุไว้ในขั้นที่ 2 */
    private List<@NotBlank String> tagNames;
}