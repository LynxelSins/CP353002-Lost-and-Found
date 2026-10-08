package com.example.lostandfound;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class LostAndFoundApplicationTest extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        assertThat(postgres.isRunning()).isTrue();
    }
}