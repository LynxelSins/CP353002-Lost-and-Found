package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.ImmutableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * ไฟล์รูปที่ผู้ใช้อัปโหลด เก็บเป็น bytes ใน PostgreSQL (Neon) โดยตรง
 * ไม่แก้ไขหลังอัปโหลด (ImmutableEntity) — ถ้าจะเปลี่ยนรูปให้อัปโหลดใหม่แล้วได้ id ใหม่
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stored_files")
public class StoredFile extends ImmutableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "file_id")
    private UUID id;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    // columnDefinition = "bytea" ให้ตรงกับ schema.sql และผ่าน ddl-auto=validate
    // (ห้ามใช้ @Lob เพราะ Hibernate จะไปคาดหวัง oid แทน bytea)
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "data", nullable = false, columnDefinition = "bytea")
    private byte[] data;
}