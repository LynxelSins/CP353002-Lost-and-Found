package com.example.lostandfound.exception;

import com.example.lostandfound.dto.response.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== helper ====================

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String error, String message) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    // ==================== 500 (catch-all) ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(Exception ex) {
        // ต้อง log เสมอ ไม่งั้นหา root cause ตอน deploy ไม่ได้
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred");
    }

    // ==================== exception ของระบบเรา ====================

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage());
    }

    /** ล็อกอินแล้วแต่ไม่มีสิทธิ์ (เช่น ไม่ใช่เจ้าของประกาศ) -> 403 เพื่อไม่ให้ frontend เข้าใจผิดว่า token หมดอายุแล้ว logout */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(ForbiddenException ex) {
        return build(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex) {
        return build(HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
    }

    // ==================== Security ====================

    /**
     * ครอบคลุมทั้ง @PreAuthorize ปฏิเสธสิทธิ์ (AccessDeniedException /
     * AuthorizationDeniedException ตัวใหม่ใน Spring Security 6) และกรณีไม่มี
     * Authentication เลย (AuthenticationCredentialsNotFoundException)
     */
    @ExceptionHandler({
        AccessDeniedException.class,
        AuthorizationDeniedException.class,
        AuthenticationCredentialsNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(RuntimeException ex) {
        return build(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), "คุณไม่มีสิทธิ์เข้าถึงทรัพยากรนี้");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return build(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase(), "กรุณาเข้าสู่ระบบก่อนใช้งาน");
    }

    // ==================== 400: request ไม่ถูกต้อง ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        // ส่งข้อความของ field แรกที่ไม่ผ่านกลับไปเป็น message เลย เพื่อให้ frontend (err.response.data.message)
        // แสดงเหตุผลจริงได้ เช่น "ต้องแนบรูปภาพอย่างน้อย 1 รูป" แทนที่จะเป็นแค่ "Invalid request body"
        String firstMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .filter(msg -> msg != null && !msg.isBlank())
                .findFirst()
                .orElse("Invalid request body");

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message(firstMessage)
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return build(HttpStatus.BAD_REQUEST, "Constraint Violation", ex.getMessage());
    }

    /** JSON พัง / ฟิลด์ผิดชนิด / enum ไม่มีค่านั้น (เช่น type=ABC) */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed Request",
                "รูปแบบข้อมูลที่ส่งมาไม่ถูกต้อง (JSON ผิดรูปแบบ หรือค่าไม่ตรงกับชนิดที่กำหนด)");
    }

    /** id ไม่ใช่ UUID, enum ใน query string ผิด เช่น /reports/abc หรือ ?status=XYZ */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Parameter",
                "พารามิเตอร์ '" + ex.getName() + "' มีรูปแบบไม่ถูกต้อง");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "Missing Parameter",
                "ต้องระบุพารามิเตอร์ '" + ex.getParameterName() + "'");
    }

    /** อัปโหลดโดยไม่แนบไฟล์ (ไม่มี part ชื่อ file) */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingPart(MissingServletRequestPartException ex) {
        return build(HttpStatus.BAD_REQUEST, "Missing Part",
                "ต้องแนบไฟล์ในฟิลด์ '" + ex.getRequestPartName() + "'");
    }

    /** sort ด้วยฟิลด์ที่ไม่มีอยู่ เช่น ?sort=abc,desc */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiErrorResponse> handleBadSort(PropertyReferenceException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Sort",
                "ไม่รองรับการเรียงตามฟิลด์ '" + ex.getPropertyName() + "'");
    }

    // ==================== 404 / 405 / 415 ====================

    /** path ที่ไม่มีอยู่จริง (Spring 6.1+) */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), "ไม่พบ endpoint ที่เรียก");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                "endpoint นี้ไม่รองรับ HTTP method " + ex.getMethod());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
                "ไม่รองรับ Content-Type ที่ส่งมา");
    }

    // ==================== 409 / 413 ====================

    /** unique/FK ชนกันที่ระดับ DB เช่น สร้างแท็กชื่อซ้ำพร้อมกัน */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(),
                "ข้อมูลซ้ำหรือถูกอ้างอิงอยู่ ไม่สามารถทำรายการนี้ได้");
    }

    /** ไฟล์อัปโหลดเกิน spring.servlet.multipart.max-file-size — ถูกโยนก่อนถึง controller */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(),
                "ไฟล์ต้องมีขนาดไม่เกิน 5MB");
    }
}