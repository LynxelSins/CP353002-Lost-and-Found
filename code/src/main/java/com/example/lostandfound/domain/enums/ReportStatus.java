package com.example.lostandfound.domain.enums;

/**
 * สถานะของประกาศ (ใช้ร่วมกับ State Pattern ใน Service Layer)
 */
public enum ReportStatus {
    OPEN,
    MATCH_PENDING,
    CLAIMED,
    CLOSED,
    REJECTED
}
