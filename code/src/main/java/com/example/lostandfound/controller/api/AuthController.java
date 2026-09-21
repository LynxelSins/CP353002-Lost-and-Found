package com.example.lostandfound.controller.api;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.dto.request.GoogleLoginRequest;
import com.example.lostandfound.dto.request.LoginRequest;
import com.example.lostandfound.dto.request.RegisterRequest;
import com.example.lostandfound.dto.response.ApiResponse;
import com.example.lostandfound.dto.response.AuthResponse;
import com.example.lostandfound.security.JwtUtil;
import com.example.lostandfound.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(AuthResponse.of(token, user)));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(user);
        return ApiResponse.success(AuthResponse.of(token, user));
    }

    @PostMapping("/google")
    public ApiResponse<AuthResponse> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        User user = authService.loginWithGoogle(request.getIdToken());
        String token = jwtUtil.generateToken(user);
        return ApiResponse.success(AuthResponse.of(token, user));
    }
}