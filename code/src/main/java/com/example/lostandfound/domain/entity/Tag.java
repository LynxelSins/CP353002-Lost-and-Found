package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * แท็กของประกาศ (Master Data) — Many-to-Many กับ Report ผ่านตารางกลาง report_tags
 * (ไม่มี Entity แยกสำหรับ report_tags เพราะไม่มีคอลัมน์อื่นนอกจาก FK สองตัว)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tags")
public class Tag extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "tag_name", nullable = false, unique = true, length = 100)
    private String tagName;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private List<Report> reports = new ArrayList<>();
}
