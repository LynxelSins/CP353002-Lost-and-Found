package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.AuditableEntity;
import com.example.lostandfound.domain.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * คำขอเคลม (ระบบเคลมเต็มรูปแบบ: ส่งหลักฐาน -&gt; แอดมินอนุมัติ/ปฏิเสธ -&gt; นัดรับ)
 * <p>
 * การกันยื่น PENDING ซ้ำซ้อนในโพสต์เดียวกันโดยคนเดิม ทำผ่าน Partial Unique Index
 * ({@code idx_unique_pending_claim}) ที่ระดับ schema.sql เนื่องจาก JPA/Hibernate
 * annotation ไม่รองรับ unique constraint แบบมีเงื่อนไข (WHERE clause)
 * — ฝั่ง Service Layer ({@code ClaimService}) ต้องตรวจสอบซ้ำอีกชั้นก่อนบันทึกด้วย (SRP)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "claims")
public class Claim extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "claim_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @Column(name = "evidence_text", columnDefinition = "TEXT")
    private String evidenceText;

    @Column(name = "evidence_image_url", length = 500)
    private String evidenceImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_status", nullable = false, length = 20)
    @Builder.Default
    private ClaimStatus claimStatus = ClaimStatus.PENDING;

    @Column(name = "meeting_location", length = 255)
    private String meetingLocation;

    @Column(name = "meeting_time")
    private LocalDateTime meetingTime;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
