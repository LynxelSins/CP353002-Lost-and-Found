package com.example.lostandfound.service;

import com.example.lostandfound.dto.request.TagRequest;
import com.example.lostandfound.dto.response.TagResponse;

import java.util.List;

public interface TagService {
    List<TagResponse> getAll();
    TagResponse getById(Long id);
    TagResponse create(TagRequest request);
    TagResponse update(Long id, TagRequest request);
    void delete(Long id);
}