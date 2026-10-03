package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.StoredFile;
import com.example.lostandfound.dto.response.StoredFileResponse;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.repository.StoredFileRepository;
import com.example.lostandfound.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final StoredFileRepository storedFileRepository;

    @Override
    @Transactional
    public UUID store(String contentType, byte[] data) {
        if (data == null || data.length == 0) {
            throw new BadRequestException("กรุณาเลือกไฟล์รูปภาพ");
        }
        if (data.length > MAX_FILE_SIZE) {
            throw new BadRequestException("ไฟล์ต้องมีขนาดไม่เกิน 5MB");
        }
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("รองรับเฉพาะไฟล์รูปภาพ (jpg, png, webp, gif)");
        }

        StoredFile saved = storedFileRepository.save(StoredFile.builder()
                .contentType(contentType)
                .fileSize((long) data.length)
                .data(data)
                .build());
        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public StoredFileResponse load(String id) {
        UUID fileId;
        try {
            fileId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("ไม่พบไฟล์ที่ต้องการ");
        }

        StoredFile file = storedFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบไฟล์ที่ต้องการ"));
        return StoredFileResponse.from(file);
    }
}