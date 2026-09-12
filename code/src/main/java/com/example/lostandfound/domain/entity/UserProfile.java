package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * ข้อมูลโปรไฟล์ของผู้ใช้ (One-to-One กับ User)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles")
public class UserProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
}
