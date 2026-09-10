-- =====================================================================
-- LOST & FOUND DATABASE SCHEMA
-- Target: Neon (PostgreSQL)
-- Idempotent: รันซ้ำได้ (drop ของเก่าก่อน)
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) EXTENSION + ENUM TYPES
-- ---------------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE user_role     AS ENUM ('USER', 'STAFF', 'ADMIN');
CREATE TYPE report_type   AS ENUM ('LOST', 'FOUND');
CREATE TYPE report_status AS ENUM ('OPEN', 'MATCH_PENDING', 'CLAIMED', 'CLOSED', 'REJECTED');
CREATE TYPE claim_status  AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

-- ---------------------------------------------------------------------
-- 2) users
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firebase_uid VARCHAR UNIQUE NOT NULL,
    email        VARCHAR UNIQUE NOT NULL,
    role         user_role NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- 3) user_profiles
-- ---------------------------------------------------------------------
CREATE TABLE user_profiles (
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id             UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name           VARCHAR NOT NULL,
    phone_number        VARCHAR,
    student_or_staff_id VARCHAR,
    avatar_url          TEXT
);

-- ---------------------------------------------------------------------
-- 4) categories
-- ---------------------------------------------------------------------
CREATE TABLE categories (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name        VARCHAR NOT NULL,
    description TEXT
);

-- ---------------------------------------------------------------------
-- 5) tags
-- ---------------------------------------------------------------------
CREATE TABLE tags (
    id   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR UNIQUE NOT NULL
);

-- ---------------------------------------------------------------------
-- 6) reports
-- ---------------------------------------------------------------------
CREATE TABLE reports (
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    reporter_id     UUID REFERENCES users(id) ON DELETE SET NULL,
    category_id     BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    type            report_type NOT NULL,
    title           VARCHAR NOT NULL,
    description     TEXT,
    location_name   VARCHAR NOT NULL,
    latitude        DECIMAL(10,8),
    longitude       DECIMAL(11,8),
    event_timestamp TIMESTAMP NOT NULL,
    status          report_status NOT NULL DEFAULT 'OPEN',
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- 7) report_images
-- ---------------------------------------------------------------------
CREATE TABLE report_images (
    id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    report_id BIGINT REFERENCES reports(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL
);

-- ---------------------------------------------------------------------
-- 8) claims
-- ---------------------------------------------------------------------
CREATE TABLE claims (
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    report_id         BIGINT REFERENCES reports(id),
    claimer_id        UUID REFERENCES users(id) ON DELETE SET NULL,
    proof_description TEXT,
    status            claim_status NOT NULL DEFAULT 'PENDING',
    created_at        TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_one_pending_claim
    ON claims (report_id, claimer_id)
    WHERE status = 'PENDING';

-- ---------------------------------------------------------------------
-- 9) report_status_logs
-- ---------------------------------------------------------------------
CREATE TABLE report_status_logs (
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    report_id  BIGINT REFERENCES reports(id) ON DELETE CASCADE,
    actor_id   UUID REFERENCES users(id) ON DELETE SET NULL,
    old_status VARCHAR,
    new_status VARCHAR,
    remark     TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- 10) report_tags (M:N)
-- ---------------------------------------------------------------------
CREATE TABLE report_tags (
    report_id  BIGINT REFERENCES reports(id) ON DELETE CASCADE,
    tag_id     BIGINT REFERENCES tags(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (report_id, tag_id)
);

-- ---------------------------------------------------------------------
-- 11) report_watchers (M:N)
-- ---------------------------------------------------------------------
CREATE TABLE report_watchers (
    user_id    UUID REFERENCES users(id) ON DELETE CASCADE,
    report_id  BIGINT REFERENCES reports(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, report_id)
);

-- ---------------------------------------------------------------------
-- 12) INDEXES
-- ---------------------------------------------------------------------
CREATE INDEX idx_reports_reporter_id     ON reports (reporter_id);
CREATE INDEX idx_reports_category_id     ON reports (category_id);
CREATE INDEX idx_reports_type            ON reports (type);
CREATE INDEX idx_reports_event_timestamp ON reports (event_timestamp);
CREATE INDEX idx_reports_status          ON reports (status);

CREATE INDEX idx_status_logs_report_id   ON report_status_logs (report_id);
CREATE INDEX idx_claims_report_id        ON claims (report_id);




-- ---------------------------------------------------------------------
-- CLEANUP (สำหรับรันซ้ำ)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS report_watchers     CASCADE;
DROP TABLE IF EXISTS report_tags         CASCADE;
DROP TABLE IF EXISTS report_status_logs  CASCADE;
DROP TABLE IF EXISTS claims              CASCADE;
DROP TABLE IF EXISTS report_images       CASCADE;
DROP TABLE IF EXISTS reports             CASCADE;
DROP TABLE IF EXISTS tags                CASCADE;
DROP TABLE IF EXISTS categories          CASCADE;
DROP TABLE IF EXISTS user_profiles       CASCADE;
DROP TABLE IF EXISTS users               CASCADE;

DROP TYPE IF EXISTS claim_status  CASCADE;
DROP TYPE IF EXISTS report_status CASCADE;
DROP TYPE IF EXISTS report_type   CASCADE;
DROP TYPE IF EXISTS user_role     CASCADE;