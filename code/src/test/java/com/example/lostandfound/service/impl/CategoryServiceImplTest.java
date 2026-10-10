package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.Category;
import com.example.lostandfound.dto.request.CategoryRequest;
import com.example.lostandfound.dto.response.CategoryResponse;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.mapper.CategoryMapper;
import com.example.lostandfound.repository.CategoryRepository;
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
class CategoryServiceImplTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getAll_shouldReturnMappedList() {
        Category category = Category.builder().id(1L).categoryName("เอกสาร").build();
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(
                CategoryResponse.builder().id(1L).categoryName("เอกสาร").build());

        List<CategoryResponse> result = categoryService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryName()).isEqualTo("เอกสาร");
    }

    @Test
    void create_shouldThrowConflict_whenNameAlreadyExists() {
        CategoryRequest request = new CategoryRequest();
        request.setCategoryName("เอกสาร");
        when(categoryRepository.existsByCategoryNameIgnoreCase("เอกสาร")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("อยู่แล้ว");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void create_shouldSucceed_whenNameNotExists() {
        CategoryRequest request = new CategoryRequest();
        request.setCategoryName("กระเป๋า");
        Category entity = Category.builder().categoryName("กระเป๋า").build();
        Category saved = Category.builder().id(2L).categoryName("กระเป๋า").build();

        when(categoryRepository.existsByCategoryNameIgnoreCase("กระเป๋า")).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(saved);
        when(categoryMapper.toResponse(saved)).thenReturn(
                CategoryResponse.builder().id(2L).categoryName("กระเป๋า").build());

        CategoryResponse result = categoryService.create(request);

        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldCallRepository_whenFound() {
        Category category = Category.builder().id(1L).categoryName("เอกสาร").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(categoryRepository).delete(category);
    }
}