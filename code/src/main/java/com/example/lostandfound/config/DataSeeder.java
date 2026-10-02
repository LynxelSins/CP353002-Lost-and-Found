package com.example.lostandfound.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

        if (userCount != null && userCount > 0) {
            log.info("มีข้อมูลใน users อยู่แล้ว ({} คน) — ข้ามการ seed ข้อมูล", userCount);
            return;
        }

        log.info("ไม่พบข้อมูลใน users — เริ่ม seed mock data จาก seed-data.sql");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("seed-data.sql"));
        populator.execute(jdbcTemplate.getDataSource());
        log.info("Seed ข้อมูลเสร็จเรียบร้อย");
    }
}