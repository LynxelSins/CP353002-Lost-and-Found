package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.ImmutableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * ประวัติการเปลี่ยนสถานะของประกาศ — เป็น log ที่ห้ามแก้ไขย้อนหลัง
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "report_status_logs")
public class ReportStatusLog extends ImmutableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(name = "old_status", length = 20)
    private String oldStatus;

    @Column(name = "new_status", nullable = false, length = 20)
    private String newStatus;
}
