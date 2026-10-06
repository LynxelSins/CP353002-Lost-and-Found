package com.example.lostandfound.service;

import com.example.lostandfound.dto.response.StoredFileResponse;

import java.util.UUID;

public interface FileStorageService {
    UUID store(String contentType, byte[] data);
    StoredFileResponse load(String id);
}