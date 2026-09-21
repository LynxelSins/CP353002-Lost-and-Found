package com.example.lostandfound.controller.api;

import com.example.lostandfound.dto.request.CategoryRequest;
import com.example.lostandfound.dto.response.CategoryResponse;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Controller นี้ไม่ใช้ @CurrentUser -> ไม่ต้องตั้ง SecurityContext เอง
@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CategoryService categoryService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserRepository userRepository;

    @Test
    void getAll_shouldReturn200_withList() throws Exception {
        when(categoryService.getAll()).thenReturn(
                List.of(CategoryResponse.builder().id(1L).categoryName("เอกสาร").build()));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].categoryName").value("เอกสาร"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(categoryService.getById(99L)).thenThrow(new ResourceNotFoundException("Category", "id", 99L));

        mockMvc.perform(get("/api/categories/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn201_whenValid() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setCategoryName("กระเป๋า");

        when(categoryService.create(any(CategoryRequest.class))).thenReturn(
                CategoryResponse.builder().id(2L).categoryName("กระเป๋า").build());

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.categoryName").value("กระเป๋า"));
    }

    @Test
    void create_shouldReturn409_whenDuplicateName() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setCategoryName("เอกสาร");

        when(categoryService.create(any(CategoryRequest.class)))
                .thenThrow(new ConflictException("มีหมวดหมู่ชื่อ 'เอกสาร' อยู่แล้ว"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void create_shouldReturn400_whenNameBlank() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setCategoryName("");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturn200() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setCategoryName("กระเป๋าเดินทาง");

        when(categoryService.update(eq(1L), any(CategoryRequest.class))).thenReturn(
                CategoryResponse.builder().id(1L).categoryName("กระเป๋าเดินทาง").build());

        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryName").value("กระเป๋าเดินทาง"));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ลบหมวดหมู่เรียบร้อย"));
    }
}