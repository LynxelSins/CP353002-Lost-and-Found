package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.Tag;
import com.example.lostandfound.dto.request.TagRequest;
import com.example.lostandfound.dto.response.TagResponse;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.mapper.TagMapper;
import com.example.lostandfound.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock private TagRepository tagRepository;
    @Mock private TagMapper tagMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    void create_shouldThrowConflict_whenTagNameExists() {
        TagRequest request = new TagRequest();
        request.setTagName("กระเป๋าสีดำ");
        when(tagRepository.existsByTagNameIgnoreCase("กระเป๋าสีดำ")).thenReturn(true);

        assertThatThrownBy(() -> tagService.create(request))
                .isInstanceOf(ConflictException.class);

        verify(tagRepository, never()).save(any());
    }

    @Test
    void create_shouldSucceed_whenTagNameNew() {
        TagRequest request = new TagRequest();
        request.setTagName("กระเป๋าสีแดง");
        Tag entity = Tag.builder().tagName("กระเป๋าสีแดง").build();
        Tag saved = Tag.builder().id(1L).tagName("กระเป๋าสีแดง").build();

        when(tagRepository.existsByTagNameIgnoreCase("กระเป๋าสีแดง")).thenReturn(false);
        when(tagMapper.toEntity(request)).thenReturn(entity);
        when(tagRepository.save(entity)).thenReturn(saved);
        when(tagMapper.toResponse(saved)).thenReturn(TagResponse.builder().id(1L).tagName("กระเป๋าสีแดง").build());

        TagResponse result = tagService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getAll_shouldReturnAll() {
        Tag tag = Tag.builder().id(1L).tagName("แว่นตา").build();
        when(tagRepository.findAll()).thenReturn(List.of(tag));
        when(tagMapper.toResponse(tag)).thenReturn(TagResponse.builder().id(1L).tagName("แว่นตา").build());

        List<TagResponse> result = tagService.getAll();

        assertThat(result).extracting(TagResponse::getTagName).containsExactly("แว่นตา");
    }

    @Test
    void delete_shouldThrow_whenTagNotFound() {
        when(tagRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.delete(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}