package com.example.lostandfound.controller.api;
import com.example.lostandfound.dto.response.UserResponse;  
import org.springframework.web.bind.annotation.GetMapping; 
import com.example.lostandfound.dto.request.ChangePasswordRequest;
import com.example.lostandfound.dto.request.UpdateProfileRequest;
import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.security.CurrentUser;
import com.example.lostandfound.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.lostandfound.dto.request.UpdateProfileRequest;
import java.util.UUID;

/**
 * เชื่อม UserService (changePassword/removePassword) เข้ากับ REST API
 * — ก่อนหน้านี้ Service เขียนไว้แล้วแต่ไม่มี Controller เรียกใช้ (dead code)
 */
@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @PatchMapping("/password")
    public ApiResponse<Void> changePassword(@CurrentUser UUID userId,
                                              @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ApiResponse.success("เปลี่ยนรหัสผ่านเรียบร้อย", null);
    }

    @GetMapping
public ApiResponse<UserResponse> getMe(@CurrentUser UUID userId) {
    return ApiResponse.success(userService.getCurrentUser(userId));
}

    @DeleteMapping("/password")
    public ApiResponse<Void> removePassword(@CurrentUser UUID userId) {
        userService.removePassword(userId);
        return ApiResponse.success("ลบรหัสผ่านเรียบร้อย (เข้าสู่ระบบได้ด้วย Google เท่านั้น)", null);
    }

        @PatchMapping
    public ApiResponse<UserResponse> updateProfile(@CurrentUser UUID userId,
                                                      @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success("บันทึกโปรไฟล์เรียบร้อย", userService.updateProfile(userId, request));
    }
}