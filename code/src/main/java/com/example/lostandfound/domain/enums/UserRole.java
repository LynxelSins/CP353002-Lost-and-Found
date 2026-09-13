package com.example.lostandfound.domain.enums;

/**
 * บทบาทของผู้ใช้งาน
 * <p>
 * STAFF เป็นผู้ที่มีสิทธิ์อนุมัติ/ปฏิเสธคำขอเคลม (Claim) ในระบบ
 * (ต้องตรงกับ {@code chk_users_role} ใน schema.sql เป๊ะ ๆ)
 */
public enum UserRole {
    USER,
    STAFF
}
