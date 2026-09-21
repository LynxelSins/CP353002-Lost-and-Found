package com.example.lostandfound.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApproveClaimRequest {
    /** ไม่บังคับ — ใส่ตอนอนุมัติเพื่อนัดจุดรับของได้เลยในขั้นตอนเดียว */
    private String meetingLocation;
    private LocalDateTime meetingTime;
}