package com.example.lostandfound.service.impl;
import com.example.lostandfound.exception.ConflictException;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.entity.UserProfile;
import com.example.lostandfound.domain.enums.UserRole;
import com.example.lostandfound.dto.request.RegisterRequest;
import com.example.lostandfound.exception.BadRequestException;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.service.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
                userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new ConflictException("อีเมลนี้ถูกใช้งานแล้ว");
        });
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(request.getFullName())
                .build();
        user.setProfile(profile);

        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("อีเมลหรือรหัสผ่านไม่ถูกต้อง"));

        if (!StringUtils.hasText(user.getPasswordHash())) {
            throw new BadRequestException("บัญชีนี้ลงทะเบียนด้วย Google กรุณาเข้าสู่ระบบผ่าน Google แทน");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadRequestException("อีเมลหรือรหัสผ่านไม่ถูกต้อง");
        }

        return user;
    }

    @Override
    @Transactional
    public User loginWithGoogle(String idToken) {
        FirebaseToken decodedToken = verifyToken(idToken);

        String firebaseUid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        String name = decodedToken.getName();

        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .map(existing -> {
                            existing.setFirebaseUid(firebaseUid);
                            return userRepository.save(existing);
                        })
                        .orElseGet(() -> createGoogleUser(firebaseUid, email, name)));
    }

    private FirebaseToken verifyToken(String idToken) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new BadRequestException("Google ID token ไม่ถูกต้องหรือหมดอายุ");
        }
    }

    private User createGoogleUser(String firebaseUid, String email, String name) {
        User newUser = User.builder()
                .email(email)
                .firebaseUid(firebaseUid)
                .role(UserRole.USER)
                .build();

        UserProfile profile = UserProfile.builder()
                .user(newUser)
                .fullName(StringUtils.hasText(name) ? name : email)
                .build();
        newUser.setProfile(profile);

        return userRepository.save(newUser);
    }
}