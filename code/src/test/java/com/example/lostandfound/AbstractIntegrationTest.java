package com.example.lostandfound;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * คลาสฐานสำหรับ integration tests ที่ต้องการฐานข้อมูล PostgreSQL จริง
 * <p>
 * Extend คลาสนี้สำหรับการทดสอบใด ๆ ที่ต้องการฐานข้อมูลจริง
 * Container จะถูกแชร์ระหว่างคลาสทดสอบทั้งหมดใน JVM เดียวกัน
 * </p>
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @SuppressWarnings("resource") // false positive: lifecycle ถูกจัดการโดย @Testcontainers + @Container อยู่แล้ว
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}