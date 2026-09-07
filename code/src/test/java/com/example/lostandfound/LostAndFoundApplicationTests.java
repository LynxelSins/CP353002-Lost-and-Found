package com.example.lostandfound;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LostAndFoundApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        // ตรวจสอบว่า Spring application context starts successfully
        //โดยใช้ฐานข้อมูล PostgreSQL จริง (ผ่าน Testcontainers)
        assertThat(postgres.isRunning()).isTrue();
    }
}
