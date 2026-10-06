package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.entity.StoredFile;
import com.example.lostandfound.repository.StoredFileRepository;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.service.impl.FileStorageServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ทดสอบการอัปโหลด/เปิดดูรูปที่เก็บใน Neon (PostgreSQL) แทน Firebase Storage
 *
 * ไม่มี static mock อีกแล้ว (ต่างจากเวอร์ชัน Firebase) จึงไม่ต้องเรียก Mockito.clearAllCaches()
 * ซึ่งเคยทำให้ @MockitoBean ของ Spring พัง
 *
 * ต้อง mock JwtUtil/UserRepository เหมือน controller test ตัวอื่น เพราะ SecurityConfig
 * ต้องการ bean สองตัวนี้ตอนสร้าง JwtAuthenticationFilter
 */
@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(FileStorageServiceImpl.class) // controller เรียก FileStorageService -> ใช้ของจริง แล้ว mock แค่ repository
class FileUploadControllerTest {

    private static final UUID FILE_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @Autowired private MockMvc mockMvc;

    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private StoredFileRepository storedFileRepository;

    // ---------------------------------------------------------------- upload

    @Test
    void upload_shouldReturn201_andLinkToOurFilesEndpoint_whenValidImage() throws Exception {
        byte[] bytes = "fake-image-bytes".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", bytes);

        when(storedFileRepository.save(any(StoredFile.class))).thenAnswer(inv -> {
            StoredFile f = inv.getArgument(0);
            f.setId(FILE_ID);
            return f;
        });

        mockMvc.perform(multipart("/api/v1/uploads").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.url", matchesPattern(
                        "^http://localhost/api/v1/files/" + FILE_ID + "$")));

        ArgumentCaptor<StoredFile> captor = ArgumentCaptor.forClass(StoredFile.class);
        verify(storedFileRepository).save(captor.capture());
        assertEquals("image/png", captor.getValue().getContentType());
        assertEquals((long) bytes.length, captor.getValue().getFileSize());
        assertArrayEquals(bytes, captor.getValue().getData());
    }

    @Test
    void upload_shouldReturn400_whenFileEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        mockMvc.perform(multipart("/api/v1/uploads").file(emptyFile))
                .andExpect(status().isBadRequest());

        verify(storedFileRepository, never()).save(any());
    }

    @Test
    void upload_shouldReturn400_whenContentTypeNotImage() throws Exception {
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "not-an-image".getBytes());

        mockMvc.perform(multipart("/api/v1/uploads").file(pdfFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("รูปภาพ")));

        verify(storedFileRepository, never()).save(any());
    }

    @Test
    void upload_shouldReturn400_whenFileExceeds5MB() throws Exception {
        MockMultipartFile bigFile = new MockMultipartFile(
                "file", "big.png", "image/png", new byte[5 * 1024 * 1024 + 1]);

        mockMvc.perform(multipart("/api/v1/uploads").file(bigFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("5MB")));

        verify(storedFileRepository, never()).save(any());
    }

    // -------------------------------------------------------------- download

    @Test
    void download_shouldReturn200_withImageBytesAndCacheHeaders_whenFileExists() throws Exception {
        byte[] bytes = new byte[] {1, 2, 3, 4, 5};
        StoredFile stored = StoredFile.builder()
                .id(FILE_ID).contentType("image/png").fileSize((long) bytes.length).data(bytes).build();
        when(storedFileRepository.findById(FILE_ID)).thenReturn(Optional.of(stored));

        mockMvc.perform(get("/api/v1/files/{id}", FILE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(bytes))
                .andExpect(header().string("Cache-Control", containsString("immutable")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    void download_shouldReturn404_whenFileNotFound() throws Exception {
        when(storedFileRepository.findById(FILE_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/files/{id}", FILE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void download_shouldReturn404_whenIdIsNotAValidUuid() throws Exception {
        mockMvc.perform(get("/api/v1/files/{id}", "not-a-uuid"))
                .andExpect(status().isNotFound());

        verify(storedFileRepository, never()).findById(any());
    }
}