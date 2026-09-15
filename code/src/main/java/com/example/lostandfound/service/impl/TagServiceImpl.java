package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.Tag;
import com.example.lostandfound.dto.request.TagRequest;
import com.example.lostandfound.dto.response.TagResponse;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.mapper.TagMapper;
import com.example.lostandfound.repository.TagRepository;
import com.example.lostandfound.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public List<TagResponse> getAll() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    public TagResponse getById(Long id) {
        return tagMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional
    public TagResponse create(TagRequest request) {
        if (tagRepository.existsByTagNameIgnoreCase(request.getTagName())) {
            throw new ConflictException("มีแท็กชื่อ '" + request.getTagName() + "' อยู่แล้ว");
        }
        Tag saved = tagRepository.save(tagMapper.toEntity(request));
        return tagMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TagResponse update(Long id, TagRequest request) {
        Tag tag = findEntityById(id);
        tagMapper.updateEntity(tag, request);
        return tagMapper.toResponse(tag);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        tagRepository.delete(findEntityById(id));
    }

    private Tag findEntityById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
    }
}