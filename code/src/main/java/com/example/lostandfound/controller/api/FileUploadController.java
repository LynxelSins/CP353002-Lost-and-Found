package com.example.lostandfound.controller.api;

import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.dto.response.StoredFileResponse;
import com.example.lostandfound.service.FileStorageService;
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
import java.util.UUID;

/**
 *   POST /api/uploads      (ต้องล็อกอิน)  -> คืน {"data":{"url":"https://.../api/files/{uuid}"}}
 *   GET  /api/files/{id}   (สาธารณะ)      -> ส่งรูปกลับ ใช้เป็น src ของ <img> ได้เลย
 */
@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    /** ถ้ากำหนด (เช่น https://api.example.com) จะใช้เป็น base ของลิงก์ ไม่งั้นดึงจาก request */
    @Value("${app.public-base-url:}")
    private String publicBaseUrl;

    @PostMapping("/api/v1/uploads")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("อัปโหลดไฟล์ไม่สำเร็จ", e);
        }

        UUID id = fileStorageService.store(file.getContentType(), bytes);
        return ApiResponse.created(Map.of("url", buildFileUrl(id)));
    }

    @GetMapping({"/api/v1/files/{id}", "/api/files/{id}"})
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        StoredFileResponse file = fileStorageService.load(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .contentLength(file.data().length)
                // id เป็น UUID สุ่มและเนื้อหาไม่เปลี่ยน -> cache ได้ยาวเลย ลดภาระ DB
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                .header("X-Content-Type-Options", "nosniff")
                .body(file.data());
    }

    private String buildFileUrl(UUID id) {
        String base = StringUtils.hasText(publicBaseUrl)
                ? publicBaseUrl.replaceAll("/+$", "")
                : ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return base + "/api/v1/files/" + id;
    }
}