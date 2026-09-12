package com.example.lostandfound.domain.entity;

import com.example.lostandfound.common.AuditableEntity;
import com.example.lostandfound.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ผู้ใช้งานระบบ (UUID PK เพื่อกัน Enumeration Attack)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "claimant")
    @Builder.Default
    private List<Claim> claims = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ReportWatcher> watchedReports = new ArrayList<>();
}
