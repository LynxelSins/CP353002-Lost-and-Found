package com.example.lostandfound.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.service-account-path}")
    private String serviceAccountPath;

    @PostConstruct
    public void init() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        Resource resource = new PathMatchingResourcePatternResolver()
                .getResource("classpath:" + serviceAccountPath);

        // ตรวจสอบว่ามีไฟล์คีย์ Firebase หรือไม่ เพื่อไม่ให้ระบบล่มในกรณีที่ยังไม่ได้ใส่ไฟล์คีย์ตอนรันพัฒนา (Dev Mode)
        if (!resource.exists()) {
            log.warn("ไม่พบไฟล์ Firebase Service Account ที่ [classpath:{}] - การตรวจสอบสิทธิ์ผ่าน Firebase จะถูกปิดการทำงานไว้ชั่วคราว", serviceAccountPath);
            return;
        }

        try (InputStream serviceAccount = resource.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("เริ่มต้นการทำงานของ FirebaseApp สำเร็จแล้ว");
        }
    }
}