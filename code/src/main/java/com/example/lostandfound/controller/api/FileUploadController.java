package com.example.lostandfound.controller.api;

import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * รับอัปโหลดรูปภาพประกอบประกาศ (report_images) เก็บไว้ในเครื่อง server แล้วคืน URL
 * กลับไปให้ frontend เก็บใน imageUrls ตอนสร้างประกาศ
 */
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class FileUploadController {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    @PostMapping
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

        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
            file.transferTo(dir.resolve(filename));

            return ApiResponse.created(Map.of("url", "/uploads/" + filename));
        } catch (IOException e) {
            throw new RuntimeException("อัปโหลดไฟล์ไม่สำเร็จ", e);
        }
    }
}