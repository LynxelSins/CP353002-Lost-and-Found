package com.example.lostandfound.dto.response;

import com.example.lostandfound.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private UserResponse user;

    public static AuthResponse of(String token, User user) {
        return AuthResponse.builder()
                .accessToken(token)
                .user(UserResponse.fromEntity(user))
                .build();
    }
}