package com.example.lostandfound.domain.enums;

/**
 * สถานะของประกาศ (ใช้ร่วมกับ State Pattern ใน Service Layer)
 * <p>
 * เส้นทางการเปลี่ยนสถานะปกติ: OPEN -&gt; MATCH_PENDING -&gt; CLAIMED -&gt; CLOSED
 * (การเปลี่ยนแปลงจะถูกบันทึกลง {@code report_status_logs} ทุกครั้ง)
 * <p>
 * หมายเหตุความหมาย REJECTED: ใช้กับ "ตัวประกาศทั้งใบ" เท่านั้น เช่น แอดมิน/STAFF
 * ปฏิเสธเพราะเป็นประกาศปลอม/สแปม/ผิดกฎ — คนละความหมายกับ {@link ClaimStatus#REJECTED}
 * ซึ่งหมายถึงคำขอเคลมรายการหนึ่งถูกปฏิเสธ (หลักฐานไม่พอ) แต่ตัวประกาศยังเปิดอยู่ปกติ
 */
public enum ReportStatus {
    OPEN,
    MATCH_PENDING,
    CLAIMED,
    CLOSED,
    REJECTED
}
