package com.example.lostandfound.mapper;

import com.example.lostandfound.domain.entity.Category;
import com.example.lostandfound.dto.request.CategoryRequest;
import com.example.lostandfound.dto.response.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .build();
    }

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .build();
    }

    public void updateEntity(Category category, CategoryRequest request) {
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
    }
}