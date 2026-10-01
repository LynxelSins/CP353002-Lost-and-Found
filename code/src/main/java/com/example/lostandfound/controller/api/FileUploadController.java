package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.entity.StoredFile;
import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.repository.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * รับอัปโหลดรูปภาพแล้วเก็บลงฐานข้อมูล (Neon/PostgreSQL) และเปิดลิงก์ให้ดูรูปได้
 *
 *   POST /api/uploads      (ต้องล็อกอิน)  -> คืน {"data":{"url":"https://.../api/files/{uuid}"}}
 *   GET  /api/files/{id}   (สาธารณะ)      -> ส่งรูปกลับ ใช้เป็น src ของ <img> ได้เลย
 *
 * ฝั่ง frontend ไม่ต้องแก้ เพราะยังรับ/ส่งเป็น "url" เหมือนเดิม
 */
@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final StoredFileRepository storedFileRepository;

    /** ถ้ากำหนด (เช่น https://api.example.com) จะใช้เป็น base ของลิงก์ ไม่งั้นดึงจาก request */
    @Value("${app.public-base-url:}")
    private String publicBaseUrl;

    @PostMapping("/api/uploads")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("กรุณาเลือกไฟล์รูปภาพ");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("ไฟล์ต้องมีขนาดไม่เกิน 5MB");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("รองรับเฉพาะไฟล์รูปภาพ (jpg, png, webp, gif)");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("อัปโหลดไฟล์ไม่สำเร็จ", e);
        }

        StoredFile saved = storedFileRepository.save(StoredFile.builder()
                .contentType(file.getContentType())
                .fileSize((long) bytes.length)
                .data(bytes)
                .build());

        return ApiResponse.created(Map.of("url", buildFileUrl(saved.getId())));
    }

    @GetMapping("/api/files/{id}")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        UUID fileId;
        try {
            fileId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("ไม่พบไฟล์ที่ต้องการ");
        }

        StoredFile file = storedFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบไฟล์ที่ต้องการ"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(file.getData().length)
                // id เป็น UUID สุ่มและเนื้อหาไม่เปลี่ยน -> cache ได้ยาวเลย ลดภาระ DB
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                .header("X-Content-Type-Options", "nosniff")
                .body(file.getData());
    }

    private String buildFileUrl(UUID id) {
        String base = StringUtils.hasText(publicBaseUrl)
                ? publicBaseUrl.replaceAll("/+$", "")
                : ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return base + "/api/files/" + id;
    }
}