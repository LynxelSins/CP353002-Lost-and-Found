package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * หมวดหมู่ของประกาศ (Master Data) — BIGINT PK เพราะเป็นข้อมูลคงที่ ไม่ต้องกัน Enumeration
 * แต่ยัง extends AuditableEntity เพราะแอดมินแก้ไขชื่อ/รายละเอียดได้ภายหลัง
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Report> reports = new ArrayList<>();
}
