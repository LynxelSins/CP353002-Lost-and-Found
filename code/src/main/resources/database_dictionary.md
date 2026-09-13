# Database Dictionary

This dictionary describes all tables defined in `schema.sql` for the **CP353002 Lost and Found System**.

---

## users
| Column | Type | Constraints | Description |
|---|---|---|---|
| `user_id` | `UUID` | Primary Key, Default `gen_random_uuid()` | Unique identifier for each user |
| `email` | `VARCHAR(255)` | `NOT NULL`, Unique (`uq_users_email`) | User email address |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` | Hashed password |
| `role` | `VARCHAR(20)` | `NOT NULL`, Default `'USER'`, Check `role IN ('USER','ADMIN')` | User role (USER or ADMIN) |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | Nullable | Record update timestamp |

**Indexes**: none defined beyond primary key.

---

## user_profiles
| Column | Type | Constraints | Description |
|---|---|---|---|
| `profile_id` | `UUID` | Primary Key, Default `gen_random_uuid()` | Unique profile identifier |
| `user_id` | `UUID` | `NOT NULL`, Foreign Key → `users(user_id)` ON DELETE CASCADE, Unique (`uq_profile_user`) ensures 1‑to‑1 relation | Associated user |
| `full_name` | `VARCHAR(150)` | `NOT NULL` | Full name of the user |
| `phone_number` | `VARCHAR(20)` | Nullable | Contact phone |
| `avatar_url` | `VARCHAR(500)` | Nullable | URL to avatar image |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` | Creation timestamp |
| `updated_at` | `TIMESTAMP` | Nullable | Update timestamp |

---

## categories
| Column | Type | Constraints | Description |
|---|---|---|---|
| `category_id` | `BIGINT` | Primary Key, Identity column (`GENERATED ALWAYS AS IDENTITY`) | Category identifier |
| `category_name` | `VARCHAR(100)` | `NOT NULL`, Unique (`uq_category_name`) | Name of the category |
| `description` | `VARCHAR(500)` | Nullable | Optional description |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` | Creation timestamp |
| `updated_at` | `TIMESTAMP` | Nullable | Update timestamp |

---

## tags
| Column | Type | Constraints | Description |
|---|---|---|---|
| `tag_id` | `BIGINT` | Primary Key, Identity column |
| `tag_name` | `VARCHAR(100)` | `NOT NULL`, Unique (`uq_tag_name`) |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` |
| `updated_at` | `TIMESTAMP` | Nullable |

---

## reports
| Column | Type | Constraints | Description |
|---|---|---|---|
| `report_id` | `UUID` | Primary Key, Default `gen_random_uuid()` |
| `user_id` | `UUID` | `NOT NULL`, FK → `users(user_id)` ON DELETE RESTRICT |
| `category_id` | `BIGINT` | `NOT NULL`, FK → `categories(category_id)` ON DELETE RESTRICT |
| `type` | `VARCHAR(20)` | `NOT NULL`, Check `type IN ('LOST','FOUND')` |
| `title` | `VARCHAR(200)` | `NOT NULL` |
| `description` | `TEXT` | Nullable |
| `location_name` | `VARCHAR(255)` | `NOT NULL` |
| `event_timestamp` | `TIMESTAMP` | `NOT NULL` |
| `status` | `VARCHAR(20)` | `NOT NULL`, Default `'OPEN'`, Check `status IN ('OPEN','MATCH_PENDING','CLAIMED','CLOSED')` |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` |
| `updated_at` | `TIMESTAMP` | Nullable |

**Indexes**:
- `idx_reports_status` on `status`
- `idx_reports_category` on `category_id`
- `idx_reports_user` on `user_id`
- `idx_reports_type` on `type`

---

## report_images
| Column | Type | Constraints | Description |
|---|---|---|---|
| `image_id` | `BIGINT` | Primary Key, Identity column |
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE CASCADE |
| `image_url` | `VARCHAR(500)` | `NOT NULL` |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` |

**Indexes**: `idx_images_report` on `report_id`

---

## report_status_logs
| Column | Type | Constraints | Description |
|---|---|---|---|
| `log_id` | `BIGINT` | Primary Key, Identity column |
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE CASCADE |
| `changed_by` | `UUID` | Nullable, FK → `users(user_id)` ON DELETE SET NULL |
| `old_status` | `VARCHAR(20)` | Nullable |
| `new_status` | `VARCHAR(20)` | `NOT NULL` |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` |

**Indexes**: `idx_logs_report_id` on `report_id`

---

## report_watchers
| Column | Type | Constraints | Description |
|---|---|---|---|
| `watch_id` | `BIGINT` | Primary Key, Identity column |
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE CASCADE |
| `user_id` | `UUID` | `NOT NULL`, FK → `users(user_id)` ON DELETE CASCADE |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` |
| **Unique** (`uq_watcher`) on (`report_id`,`user_id`) to prevent duplicate watches |

**Indexes**: `idx_watchers_report` on `report_id`; `idx_watchers_user` on `user_id`

---

## claims
| Column | Type | Constraints | Description |
|---|---|---|---|
| `claim_id` | `UUID` | Primary Key, Default `gen_random_uuid()` |
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE RESTRICT |
| `claimant_id` | `UUID` | `NOT NULL`, FK → `users(user_id)` ON DELETE RESTRICT |
| `evidence_text` | `TEXT` | Nullable |
| `evidence_image_url` | `VARCHAR(500)` | Nullable |
| `claim_status` | `VARCHAR(20)` | `NOT NULL`, Default `'PENDING'`, Check `claim_status IN ('PENDING','APPROVED','REJECTED')` |
| `meeting_location` | `VARCHAR(255)` | Nullable |
| `meeting_time` | `TIMESTAMP` | Nullable |
| `resolved_at` | `TIMESTAMP` | Nullable |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` |
| `updated_at` | `TIMESTAMP` | Nullable |

**Indexes**: `idx_claims_report` on `report_id`; `idx_claims_claimant` on `claimant_id`

**Partial Unique Index**: `idx_unique_pending_claim` ensures a user can have only one pending claim per report.

---

## report_tags (junction table)
| Column | Type | Constraints | Description |
|---|---|---|---|
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE CASCADE |
| `tag_id` | `BIGINT` | `NOT NULL`, FK → `tags(tag_id)` ON DELETE CASCADE |
| `created_at` | `TIMESTAMP` | `NOT NULL`, Default `now()` |
| **Primary Key** (`report_id`, `tag_id`) |

**Indexes**: `idx_reporttags_tag` on `tag_id`

---

*All timestamps use the server's time zone unless otherwise configured.*
