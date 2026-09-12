-- ===================================================================
-- CP353002 Lost and Found System - Database Schema
-- Source of Truth (ddl-auto=validate: Hibernate จะไม่สร้าง/แก้ตารางเอง)
--
-- ID Strategy (Hybrid):
--   - UUID  : ตารางที่ผู้ใช้เข้าถึงผ่าน URL ได้ตรง ๆ (users, user_profiles, reports, claims)
--             -> กัน Enumeration Attack และซ่อนจำนวนข้อมูลในระบบ
--   - BIGINT: Master data และตารางภายใน (categories, tags, report_images,
--             report_watchers, report_status_logs) -> เร็วกว่า ประหยัดพื้นที่กว่า
--
-- Audit Strategy:
--   - AuditableEntity : created_at + updated_at (แก้ไขได้)
--   - ImmutableEntity : created_at เท่านั้น (log/history ห้ามแก้ไข)
-- ===================================================================

-- ต้องเปิดใช้ก่อนถ้า Postgres ยังไม่มี gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ===================================================================
-- 1. users  (UUID, AuditableEntity)
-- ===================================================================
CREATE TABLE IF NOT EXISTS users (
    user_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER', -- USER, STAFF
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('USER', 'STAFF'))
);

-- ===================================================================
-- 2. user_profiles  (UUID, AuditableEntity)  -- 1:1 กับ users
-- ===================================================================
CREATE TABLE IF NOT EXISTS user_profiles (
    profile_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL,
    full_name    VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20),
    avatar_url   VARCHAR(500),
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT uq_profile_user UNIQUE (user_id)  -- บังคับ 1:1
);

-- ===================================================================
-- 3. categories  (BIGINT, AuditableEntity)  -- Master data แต่แก้ไขได้
-- ===================================================================
CREATE TABLE IF NOT EXISTS categories (
    category_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description   VARCHAR(500),
    created_at    TIMESTAMP NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP,
    CONSTRAINT uq_category_name UNIQUE (category_name)
);

-- ===================================================================
-- 4. tags  (BIGINT, AuditableEntity)
-- ===================================================================
CREATE TABLE IF NOT EXISTS tags (
    tag_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tag_name   VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    CONSTRAINT uq_tag_name UNIQUE (tag_name)
);

-- ===================================================================
-- 5. reports  (UUID, AuditableEntity)
-- ===================================================================
CREATE TABLE IF NOT EXISTS reports (
    report_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID,
    category_id     BIGINT,
    type            VARCHAR(20) NOT NULL,                    -- LOST, FOUND
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    location_name   VARCHAR(255) NOT NULL,
    event_timestamp TIMESTAMP NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'OPEN',      -- OPEN, MATCH_PENDING, CLAIMED, CLOSED, REJECTED
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE SET NULL,
    CONSTRAINT fk_report_category FOREIGN KEY (category_id)
        REFERENCES categories (category_id) ON DELETE SET NULL,
    CONSTRAINT chk_report_type CHECK (type IN ('LOST', 'FOUND')),
    CONSTRAINT chk_report_status CHECK (status IN ('OPEN', 'MATCH_PENDING', 'CLAIMED', 'CLOSED', 'REJECTED'))
);

CREATE INDEX IF NOT EXISTS idx_reports_status   ON reports (status);
CREATE INDEX IF NOT EXISTS idx_reports_category ON reports (category_id);
CREATE INDEX IF NOT EXISTS idx_reports_user     ON reports (user_id);
CREATE INDEX IF NOT EXISTS idx_reports_type     ON reports (type);

-- ===================================================================
-- 6. report_images  (BIGINT, ImmutableEntity)  -- 1 report : N images
-- ===================================================================
CREATE TABLE IF NOT EXISTS report_images (
    image_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    report_id   UUID NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_image_report FOREIGN KEY (report_id)
        REFERENCES reports (report_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_images_report ON report_images (report_id);

-- ===================================================================
-- 7. report_status_logs  (BIGINT, ImmutableEntity)  -- history, ห้ามแก้ไข
-- ===================================================================
CREATE TABLE IF NOT EXISTS report_status_logs (
    log_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    report_id   UUID NOT NULL,
    changed_by  UUID,                       -- SET NULL ได้: log ต้องอยู่ต่อแม้ user จะถูกลบ
    old_status  VARCHAR(20),
    new_status  VARCHAR(20) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_log_report FOREIGN KEY (report_id)
        REFERENCES reports (report_id) ON DELETE CASCADE,
    CONSTRAINT fk_log_user FOREIGN KEY (changed_by)
        REFERENCES users (user_id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_logs_report_id ON report_status_logs (report_id);

-- ===================================================================
-- 8. report_watchers  (BIGINT, ImmutableEntity)  -- 1 คน watch หลาย report
-- ===================================================================
CREATE TABLE IF NOT EXISTS report_watchers (
    watch_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    report_id   UUID NOT NULL,
    user_id     UUID NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_watcher_report FOREIGN KEY (report_id)
        REFERENCES reports (report_id) ON DELETE CASCADE,
    CONSTRAINT fk_watcher_user FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT uq_watcher UNIQUE (report_id, user_id)   -- กันกด watch ซ้ำ
);

CREATE INDEX IF NOT EXISTS idx_watchers_report ON report_watchers (report_id);
CREATE INDEX IF NOT EXISTS idx_watchers_user   ON report_watchers (user_id);

-- ===================================================================
-- 9. claims  (UUID, AuditableEntity)  -- ระบบเคลมเต็มรูปแบบ
-- ===================================================================
CREATE TABLE IF NOT EXISTS claims (
    claim_id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id           UUID NOT NULL,
    claimant_id         UUID NOT NULL,
    evidence_text       TEXT,
    evidence_image_url  VARCHAR(500),
    claim_status        VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    meeting_location     VARCHAR(255),
    meeting_time         TIMESTAMP,
    resolved_at         TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP,
    CONSTRAINT fk_claim_report FOREIGN KEY (report_id)
        REFERENCES reports (report_id) ON DELETE RESTRICT,
    CONSTRAINT fk_claim_user FOREIGN KEY (claimant_id)
        REFERENCES users (user_id) ON DELETE RESTRICT,
    CONSTRAINT chk_claim_status CHECK (claim_status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE INDEX IF NOT EXISTS idx_claims_report ON claims (report_id);
CREATE INDEX IF NOT EXISTS idx_claims_claimant ON claims (claimant_id);

-- กันยื่น PENDING ซ้ำซ้อนในโพสต์เดียวกันโดยคนเดิม (Partial Unique Index)
-- ทำผ่าน JPA annotation ไม่ได้ ต้องประกาศที่นี่เท่านั้น
CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_pending_claim
    ON claims (report_id, claimant_id)
    WHERE claim_status = 'PENDING';

-- ===================================================================
-- 10. report_tags  (Composite PK, ไม่มี Entity/Repository แยก)
--     Many-to-Many ระหว่าง reports <-> tags ผ่าน @ManyToMany + @JoinTable
-- ===================================================================
CREATE TABLE IF NOT EXISTS report_tags (
    report_id  UUID NOT NULL,
    tag_id     BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (report_id, tag_id),
    CONSTRAINT fk_reporttag_report FOREIGN KEY (report_id)
        REFERENCES reports (report_id) ON DELETE CASCADE,
    CONSTRAINT fk_reporttag_tag FOREIGN KEY (tag_id)
        REFERENCES tags (tag_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_reporttags_tag ON report_tags (tag_id);