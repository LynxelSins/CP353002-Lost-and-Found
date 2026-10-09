package com.example.lostandfound.service.impl;

import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.dto.request.RegisterRequest;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("new@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("New User");
    }

    @Test
    void register_shouldCreateUser_whenEmailNotUsed() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register(registerRequest);

        assertThat(result.getEmail()).isEqualTo("new@test.com");
        assertThat(result.getPasswordHash()).isEqualTo("hashed");
        assertThat(result.getRole()).isEqualTo(UserRole.USER);
        assertThat(result.getProfile().getFullName()).isEqualTo("New User");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowConflict_whenEmailAlreadyUsed() {
        when(userRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.of(User.builder().email(registerRequest.getEmail()).build()));

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ถูกใช้งานแล้ว");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnUser_whenCredentialsCorrect() {
        User user = User.builder().email("a@test.com").passwordHash("hashed").build();
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "hashed")).thenReturn(true);

        User result = authService.login("a@test.com", "plain");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void login_shouldThrowBadRequest_whenEmailNotFound() {
        when(userRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("nobody@test.com", "x"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void login_shouldThrowBadRequest_whenGoogleOnlyAccountHasNoPassword() {
        User user = User.builder().email("g@test.com").passwordHash(null).build();
        when(userRepository.findByEmail("g@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login("g@test.com", "x"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Google");
    }

    @Test
    void login_shouldThrowBadRequest_whenPasswordWrong() {
        User user = User.builder().email("a@test.com").passwordHash("hashed").build();
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("a@test.com", "wrong"))
                .isInstanceOf(BadRequestException.class);
    }
}