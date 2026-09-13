package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.AuditableEntity;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * ประกาศแจ้งของหาย/พบของ — Entity หลักของระบบ
 * <p>
 * สถานะ (status) เปลี่ยนผ่าน Service Layer ด้วย State Pattern และทุกครั้งที่เปลี่ยน
 * จะถูกบันทึกลง {@link ReportStatusLog} พร้อม publish event แจ้งเตือนไปยัง
 * {@link ReportWatcher} ทุกคน (Observer Pattern)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reports")
public class Report extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "report_id")
    private UUID id;

    /**
     * nullable ตาม schema.sql (ON DELETE SET NULL) — ถ้า User ต้นเรื่องถูกลบบัญชี
     * Report จะยังอยู่ต่อแต่ไม่มีเจ้าของ ทุกจุดที่ใช้ field นี้ต้องเช็ค null เอง
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * nullable ตาม schema.sql (ON DELETE SET NULL) — ถ้าหมวดหมู่ถูกลบ
     * Report จะยังอยู่ต่อแต่ไม่มีหมวดหมู่ ทุกจุดที่ใช้ field นี้ต้องเช็ค null เอง
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ReportType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_name", nullable = false, length = 255)
    private String locationName;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReportStatus status = ReportStatus.OPEN;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReportImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReportStatusLog> statusLogs = new ArrayList<>();

    @OneToMany(mappedBy = "report")
    @Builder.Default
    private List<Claim> claims = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReportWatcher> watchers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "report_tags",
            joinColumns = @JoinColumn(name = "report_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();
}
