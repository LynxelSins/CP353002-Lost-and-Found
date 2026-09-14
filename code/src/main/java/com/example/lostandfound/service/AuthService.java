package com.example.lostandfound.service;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.dto.request.RegisterRequest;

public interface AuthService {

    User register(RegisterRequest request);

    User login(String email, String password);

    User loginWithGoogle(String idToken);
}