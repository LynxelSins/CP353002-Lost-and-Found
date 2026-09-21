package com.example.lostandfound.controller.api;

import com.example.lostandfound.dto.request.TagRequest;
import com.example.lostandfound.dto.response.TagResponse;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.exception.ResourceNotFoundException;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.service.TagService;
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

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
class TagControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TagService tagService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserRepository userRepository;

    @Test
    void getAll_shouldReturn200() throws Exception {
        when(tagService.getAll()).thenReturn(List.of(TagResponse.builder().id(1L).tagName("แว่นตา").build()));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].tagName").value("แว่นตา"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(tagService.getById(5L)).thenThrow(new ResourceNotFoundException("Tag", "id", 5L));

        mockMvc.perform(get("/api/tags/{id}", 5L))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn201_whenValid() throws Exception {
        TagRequest request = new TagRequest();
        request.setTagName("กระเป๋าสีแดง");

        when(tagService.create(any(TagRequest.class))).thenReturn(
                TagResponse.builder().id(1L).tagName("กระเป๋าสีแดง").build());

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.tagName").value("กระเป๋าสีแดง"));
    }

    @Test
    void create_shouldReturn409_whenTagNameDuplicate() throws Exception {
        TagRequest request = new TagRequest();
        request.setTagName("กระเป๋าสีดำ");

        when(tagService.create(any(TagRequest.class)))
                .thenThrow(new ConflictException("มีแท็กชื่อ 'กระเป๋าสีดำ' อยู่แล้ว"));

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_shouldReturn200() throws Exception {
        TagRequest request = new TagRequest();
        request.setTagName("แว่นกันแดด");

        when(tagService.update(eq(1L), any(TagRequest.class))).thenReturn(
                TagResponse.builder().id(1L).tagName("แว่นกันแดด").build());

        mockMvc.perform(put("/api/tags/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tagName").value("แว่นกันแดด"));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/tags/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ลบแท็กเรียบร้อย"));
    }
}