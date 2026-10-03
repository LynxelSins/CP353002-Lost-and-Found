package com.example.lostandfound.controller.api;

import com.example.lostandfound.dto.request.ChangePasswordRequest;
import com.example.lostandfound.dto.request.UpdateProfileRequest;
import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.dto.response.UserResponse;
import com.example.lostandfound.security.CurrentUser;
import com.example.lostandfound.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * เชื่อม UserService (changePassword/removePassword) เข้ากับ REST API
 */
@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<UserResponse> getMe(@CurrentUser UUID userId) {
        return ApiResponse.success(userService.getCurrentUser(userId));
    }

    @PatchMapping
    public ApiResponse<UserResponse> updateProfile(@CurrentUser UUID userId,
                                                   @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success("บันทึกโปรไฟล์เรียบร้อย", userService.updateProfile(userId, request));
    }

    @PatchMapping("/password")
    public ApiResponse<Void> changePassword(@CurrentUser UUID userId,
                                            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ApiResponse.success("เปลี่ยนรหัสผ่านเรียบร้อย", null);
    }

    @DeleteMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePassword(@CurrentUser UUID userId) {
        userService.removePassword(userId);
    }
}