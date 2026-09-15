package com.example.lostandfound.mapper;

import com.example.lostandfound.domain.entity.Tag;
import com.example.lostandfound.dto.request.TagRequest;
import com.example.lostandfound.dto.response.TagResponse;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagResponse toResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .build();
    }

    public Tag toEntity(TagRequest request) {
        return Tag.builder()
                .tagName(request.getTagName())
                .build();
    }

    public void updateEntity(Tag tag, TagRequest request) {
        tag.setTagName(request.getTagName());
    }
}