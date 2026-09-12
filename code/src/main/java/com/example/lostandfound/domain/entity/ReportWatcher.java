package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.ImmutableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * ผู้ติดตามประกาศ — กด "Watch" แล้วจะได้รับแจ้งเตือนเมื่อสถานะประกาศเปลี่ยน (Observer Pattern)
 * มี unique constraint (report_id, user_id) ที่ schema.sql กันกด watch ซ้ำ
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "report_watchers")
public class ReportWatcher extends ImmutableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watch_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
