# Database Dictionary

This dictionary describes all **12 tables** defined in `schema.sql` for the **CP353002 Lost and Found System** (PostgreSQL).

## Conventions

**ID strategy (Hybrid)**
- `UUID` — tables reachable by users through URLs (`users`, `user_profiles`, `reports`, `claims`, `stored_files`); prevents enumeration attacks and hides record counts.
- `BIGINT IDENTITY` — master data and internal tables (`categories`, `tags`, `report_images`, `report_status_logs`, `report_watchers`, `notifications`).

**Audit strategy**
- `AuditableEntity` — has `created_at` + `updated_at` (rows can be edited).
- `ImmutableEntity` — has `created_at` only (rows are never edited after creation).

**Table overview**

| # | Table | PK type | Audit | Purpose |
|---|---|---|---|---|
| 1 | `users` | UUID | Auditable | Account and login credentials |
| 2 | `user_profiles` | UUID | Auditable | Personal information (1:1 with users) |
| 3 | `categories` | BIGINT | Auditable | Report categories |
| 4 | `tags` | BIGINT | Auditable | Free-form labels for reports |
| 5 | `reports` | UUID | Auditable | Lost / found announcements |
| 6 | `report_images` | BIGINT | Immutable | Images attached to a report |
| 7 | `report_status_logs` | BIGINT | Immutable | History of report status changes |
| 8 | `report_watchers` | BIGINT | Immutable | Users following a report |
| 9 | `claims` | UUID | Auditable | Claim requests for a report |
| 10 | `report_tags` | Composite | — | Junction table: reports ↔ tags |
| 11 | `notifications` | BIGINT | Immutable (+ `is_read`) | In-app notifications |
| 12 | `stored_files` | UUID | Immutable | Uploaded image binaries |

---

## 1. users

| Column | Type | Constraints | Description |
|---|---|---|---|
| `user_id` | `UUID` | PK, default `gen_random_uuid()` | Unique identifier of the user |
| `email` | `VARCHAR(255)` | `NOT NULL`, unique (`uq_users_email`) | Email address |
| `password_hash` | `VARCHAR(255)` | Nullable | Hashed password; `NULL` for Google-only accounts |
| `firebase_uid` | `VARCHAR(128)` | Nullable, unique (`uq_users_firebase_uid`) | Firebase UID; set only for users who signed in with Google |
| `role` | `VARCHAR(20)` | `NOT NULL`, default `'USER'`, check `IN ('USER','STAFF')` | User role; `STAFF` can approve / reject claims and reject reports |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | Creation timestamp |
| `updated_at` | `TIMESTAMP` | Nullable | Last update timestamp |

**Check constraint:** `chk_users_auth_method` — `password_hash IS NOT NULL OR firebase_uid IS NOT NULL`. Every user must have at least one login method (both may be set).

**Indexes:** none beyond the PK and unique constraints.

---

## 2. user_profiles

| Column | Type | Constraints | Description |
|---|---|---|---|
| `profile_id` | `UUID` | PK, default `gen_random_uuid()` | Unique identifier of the profile |
| `user_id` | `UUID` | `NOT NULL`, FK → `users(user_id)` ON DELETE CASCADE, unique (`uq_profile_user`) | Owner of the profile; the unique constraint enforces the 1:1 relation |
| `full_name` | `VARCHAR(150)` | `NOT NULL` | Full name |
| `phone_number` | `VARCHAR(20)` | Nullable | Contact phone number |
| `avatar_url` | `VARCHAR(500)` | Nullable | URL of the avatar image |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | Creation timestamp |
| `updated_at` | `TIMESTAMP` | Nullable | Last update timestamp |

---

## 3. categories

| Column | Type | Constraints | Description |
|---|---|---|---|
| `category_id` | `BIGINT` | PK, `GENERATED ALWAYS AS IDENTITY` | Category identifier |
| `category_name` | `VARCHAR(100)` | `NOT NULL`, unique (`uq_category_name`) | Category name |
| `description` | `VARCHAR(500)` | Nullable | Optional description |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | Creation timestamp |
| `updated_at` | `TIMESTAMP` | Nullable | Last update timestamp |

---

## 4. tags

| Column | Type | Constraints | Description |
|---|---|---|---|
| `tag_id` | `BIGINT` | PK, `GENERATED ALWAYS AS IDENTITY` | Tag identifier |
| `tag_name` | `VARCHAR(100)` | `NOT NULL`, unique (`uq_tag_name`) | Tag name |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | Creation timestamp |
| `updated_at` | `TIMESTAMP` | Nullable | Last update timestamp |

---

## 5. reports

| Column | Type | Constraints | Description |
|---|---|---|---|
| `report_id` | `UUID` | PK, default `gen_random_uuid()` | Unique identifier of the report |
| `user_id` | `UUID` | Nullable, FK → `users(user_id)` ON DELETE SET NULL | Reporter; the report survives if the user is deleted |
| `category_id` | `BIGINT` | Nullable, FK → `categories(category_id)` ON DELETE SET NULL | Category; the report survives if the category is deleted |
| `type` | `VARCHAR(20)` | `NOT NULL`, check `IN ('LOST','FOUND')` | Kind of announcement |
| `title` | `VARCHAR(200)` | `NOT NULL` | Short title |
| `description` | `TEXT` | Nullable | Detailed description |
| `location_name` | `VARCHAR(255)` | `NOT NULL` | Place where the item was lost / found |
| `event_timestamp` | `TIMESTAMP` | `NOT NULL` | When the item was lost / found |
| `status` | `VARCHAR(20)` | `NOT NULL`, default `'OPEN'`, check `IN ('OPEN','MATCH_PENDING','CLAIMED','CLOSED','REJECTED')` | Current status of the report |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | Creation timestamp |
| `updated_at` | `TIMESTAMP` | Nullable | Last update timestamp |

**Indexes:**
- `idx_reports_status` on `status`
- `idx_reports_category` on `category_id`
- `idx_reports_user` on `user_id`
- `idx_reports_type` on `type`

---

## 6. report_images

| Column | Type | Constraints | Description |
|---|---|---|---|
| `image_id` | `BIGINT` | PK, `GENERATED ALWAYS AS IDENTITY` | Image identifier |
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE CASCADE | Report the image belongs to |
| `image_url` | `VARCHAR(500)` | `NOT NULL` | URL of the image |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | Creation timestamp |

**Indexes:** `idx_images_report` on `report_id`

---

## 7. report_status_logs

| Column | Type | Constraints | Description |
|---|---|---|---|
| `log_id` | `BIGINT` | PK, `GENERATED ALWAYS AS IDENTITY` | Log identifier |
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE CASCADE | Report whose status changed |
| `changed_by` | `UUID` | Nullable, FK → `users(user_id)` ON DELETE SET NULL | User who made the change; the log survives if the user is deleted |
| `old_status` | `VARCHAR(20)` | Nullable | Previous status (`NULL` for the first log of a report) |
| `new_status` | `VARCHAR(20)` | `NOT NULL` | New status |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | When the change happened |

**Indexes:** `idx_logs_report_id` on `report_id`

---

## 8. report_watchers

| Column | Type | Constraints | Description |
|---|---|---|---|
| `watch_id` | `BIGINT` | PK, `GENERATED ALWAYS AS IDENTITY` | Watch identifier |
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE CASCADE | Report being watched |
| `user_id` | `UUID` | `NOT NULL`, FK → `users(user_id)` ON DELETE CASCADE | Watching user |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | When the user started watching |

**Unique constraint:** `uq_watcher` on (`report_id`, `user_id`) — a user cannot watch the same report twice.

**Indexes:** `idx_watchers_report` on `report_id`; `idx_watchers_user` on `user_id`

---

## 9. claims

| Column | Type | Constraints | Description |
|---|---|---|---|
| `claim_id` | `UUID` | PK, default `gen_random_uuid()` | Unique identifier of the claim |
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE CASCADE | Claimed report; deleting a report deletes its claims |
| `claimant_id` | `UUID` | `NOT NULL`, FK → `users(user_id)` ON DELETE RESTRICT | User who submitted the claim; a user with existing claims cannot be deleted |
| `evidence_text` | `TEXT` | Nullable | Evidence described in text |
| `evidence_image_url` | `VARCHAR(500)` | Nullable | Evidence image URL |
| `claim_status` | `VARCHAR(20)` | `NOT NULL`, default `'PENDING'`, check `IN ('PENDING','APPROVED','REJECTED')` | Claim status |
| `meeting_location` | `VARCHAR(255)` | Nullable | Pickup location, set when the claim is approved |
| `meeting_time` | `TIMESTAMP` | Nullable | Pickup time, set when the claim is approved |
| `resolved_at` | `TIMESTAMP` | Nullable | When the claim was approved / rejected |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | Creation timestamp |
| `updated_at` | `TIMESTAMP` | Nullable | Last update timestamp |

**Indexes:** `idx_claims_report` on `report_id`; `idx_claims_claimant` on `claimant_id`

**Partial unique index:** `idx_unique_pending_claim` on (`report_id`, `claimant_id`) `WHERE claim_status = 'PENDING'` — a user can have only one pending claim per report at a time.

---

## 10. report_tags (junction table)

| Column | Type | Constraints | Description |
|---|---|---|---|
| `report_id` | `UUID` | `NOT NULL`, FK → `reports(report_id)` ON DELETE CASCADE | Tagged report |
| `tag_id` | `BIGINT` | `NOT NULL`, FK → `tags(tag_id)` ON DELETE CASCADE | Tag applied |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | When the tag was applied |

**Primary key:** composite (`report_id`, `tag_id`) — the same tag cannot be applied to a report twice. Mapped with `@ManyToMany` + `@JoinTable`, so there is no separate entity or repository.

**Indexes:** `idx_reporttags_tag` on `tag_id`

---

## 11. notifications

| Column | Type | Constraints | Description |
|---|---|---|---|
| `notification_id` | `BIGINT` | PK, `GENERATED ALWAYS AS IDENTITY` | Notification identifier |
| `recipient_id` | `UUID` | `NOT NULL`, FK → `users(user_id)` ON DELETE CASCADE | User who receives the notification |
| `message` | `VARCHAR(500)` | `NOT NULL` | Notification text |
| `is_read` | `BOOLEAN` | `NOT NULL`, default `FALSE` | Whether the recipient has read it (the only mutable column) |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | Creation timestamp |

**Indexes:** `idx_notifications_recipient` on (`recipient_id`, `created_at DESC`) — supports listing a user's latest notifications.

---

## 12. stored_files

| Column | Type | Constraints | Description |
|---|---|---|---|
| `file_id` | `UUID` | PK, default `gen_random_uuid()` | Unique identifier of the file |
| `content_type` | `VARCHAR(100)` | `NOT NULL` | MIME type, e.g. `image/png` |
| `file_size` | `BIGINT` | `NOT NULL` | File size in bytes |
| `data` | `BYTEA` | `NOT NULL` | Binary content of the uploaded file |
| `created_at` | `TIMESTAMP` | `NOT NULL`, default `now()` | Upload timestamp |

---

## Relationship summary

| Relationship | Type | Delete behaviour |
|---|---|---|
| `users` — `user_profiles` | 1:1 | CASCADE |
| `users` → `reports` | 1:N | SET NULL |
| `categories` → `reports` | 1:N | SET NULL |
| `reports` → `report_images` | 1:N | CASCADE |
| `reports` → `report_status_logs` | 1:N | CASCADE |
| `users` → `report_status_logs` (`changed_by`) | 1:N | SET NULL |
| `reports` → `claims` | 1:N | CASCADE |
| `users` → `claims` (`claimant_id`) | 1:N | RESTRICT |
| `reports` ↔ `users` via `report_watchers` | M:N | CASCADE |
| `reports` ↔ `tags` via `report_tags` | M:N | CASCADE |
| `users` → `notifications` | 1:N | CASCADE |

*`stored_files` has no foreign keys; it is referenced by URL from image columns.*

*All timestamps use the server's time zone unless otherwise configured.*
