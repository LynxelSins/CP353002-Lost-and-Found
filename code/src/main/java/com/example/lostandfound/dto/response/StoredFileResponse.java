package com.example.lostandfound.dto.response;

import com.example.lostandfound.domain.entity.StoredFile;

public record StoredFileResponse(String contentType, byte[] data) {

    public static StoredFileResponse from(StoredFile file) {
        return new StoredFileResponse(file.getContentType(), file.getData());
    }
}