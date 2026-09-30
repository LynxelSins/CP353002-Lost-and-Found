package com.example.lostandfound.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ล็อกอินแล้ว แต่ไม่มีสิทธิ์ทำรายการนี้ (เช่น ไม่ใช่เจ้าของประกาศ) -> 403
 * แยกจาก UnauthorizedException (401 = ยังไม่ล็อกอิน/token ไม่ถูกต้อง)
 * เพราะ frontend จะเตะผู้ใช้ออกจากระบบทุกครั้งที่ได้ 401
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}