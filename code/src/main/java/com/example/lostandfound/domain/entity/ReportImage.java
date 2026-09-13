package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.ImmutableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * รูปภาพประกอบประกาศ — ไม่แก้ไขหลังอัปโหลด (ลบ/เพิ่มใหม่แทน) จึงเป็น ImmutableEntity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "report_images")
public class ReportImage extends ImmutableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;
}
