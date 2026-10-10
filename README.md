# ระบบส่งคืนของหาย Lost and Found (กลุ่มที่ 12)
ระบบแจ้งของหายและติดตามการส่งคืนของที่พบ (Traffy Fondue Style) พัฒนาด้วย Spring Boot 3.4.3 ตามสถาปัตยกรรมแบบ Layered Architecture พร้อมเชื่อมต่อกับฐานข้อมูล PostgreSQL บน Neon Cloud เพื่อให้การติดตามสถานะสิ่งของ การพิสูจน์ความเป็นเจ้าของ และการส่งมอบของคืนเป็นไปอย่างมีประสิทธิภาพ โปร่งใส และตรวจสอบได้จริง

## สมาชิกกลุ่ม (กลุ่มที่ 12)
| ลำดับ | ชื่อ-นามสกุล | รหัสนักศึกษา | Email | Section | Branch| หน้าที่รับผิดชอบ |
| :---: | :--- | :---: | :--- | :---: | :--- | :--- |
| 1 | นางสาวณัฐนันทน์ บุษดี| 673380037-1 | natthanan.bo@kkumail.com | 01 | `natthanan_6733800371_01` | **Backend Data Layer & Document Master:** Database Schema, Entity, Spring Data JPA Repository, คุมภาพรวมเอกสารและ Diagrams |
| 2 | นางสาวศิริรัตน์ ชัยชนะ| 673380060-6 | sirirat.chai@kkumail.com | 01 | `sirirat_6733800606_01` | **Core Backend Logic & API Service:** REST Controllers, Service Layer, DTO/Mapper, Validation, Swagger/OpenAPI |
| 3 | นางสาวปภาวรินทร์ นาเมืองรักษ์| 673380275-5 | phapawarin.n@kkumail.com | 02 | `phapawarin_6733802755_02` | **System Architecture & Cloud/DevOps:** System Boilerplate, Neon Cloud DB, Dockerfile, docker-compose.yml, Cloud Deployment & CI/CD |
| 4 | นางสาวปรนันท์ บุสดีวงค์| 673380276-3 | poranun.b@kkumail.com | 02 | `poranun_6733802763_02` | **QA Specialist:** API & System Testing, JUnit 5/Mockito Tests, Demo & Presentation |
| 5 | นางสาววิภาวี ฤทธิหาญ| 673380514-3 | wiphawi_ri@kkumail.com | 01 | `wiphawi_6733805143_01` | **Frontend Lead Developer:** Core Web UI Components, Page Flow, Layout, State Management & API Integration |

> **คำเตือนเรื่อง Branch:** รูปแบบชื่อ branch ต้องเป็น `ชื่อจริง_รหัสนักศึกษา_section`

ดูรายงานได้ที่ [รายงาน-Lost-and-Found.pdf](doc/รายงาน-Lost-and-Found.pdf)

---

## Tech Stack
| หมวดหมู่ | เทคโนโลยีที่ใช้ |
| :--- | :--- |
| Backend Framework | Spring Boot 3.4.3 (Java 17) |
| Build Tool | Maven |
| Database | PostgreSQL 16 (Neon Cloud สำหรับระบบที่ Deploy, Docker สำหรับรันในเครื่อง) |
| ORM | Spring Data JPA (Hibernate) |
| Authentication | Spring Security + JWT, Firebase Authentication (Google Login) |
| API Documentation | Swagger / OpenAPI (Springdoc) |
| Frontend | React (Vite) |
| Testing | JUnit 5, Mockito, Spring Boot Test, Testcontainers, JaCoCo 0.8.12 |
| Containerization | Docker, Docker Compose |
| Deployment | Render |
| CI/CD | GitHub Actions |
| Version Control | Git, GitHub |

---

## System Architecture
ระบบใช้สถาปัตยกรรมแบบ Layered Architecture แบ่งเป็น 4 ชั้น โดยแต่ละชั้นเรียกใช้ชั้นที่อยู่ถัดไปเท่านั้น (Controller ไม่เรียก Repository โดยตรง)

- **Presentation Layer:** `controller/api` (RestController) รับคำร้อง HTTP และส่งผลลัพธ์เป็น JSON
- **Service Layer:** `service` และ `service/impl` ประมวลผล Business Logic และควบคุม Transaction
- **Repository Layer:** `repository` เข้าถึงข้อมูลผ่าน Spring Data JPA
- **Domain / Entity:** `domain/entity`, `domain/enums` นิยามโครงสร้างข้อมูลหลักของระบบ

ชั้นสนับสนุน ได้แก่ `dto` + `mapper` (แยก Entity ออกจาก API Contract), `exception` (Global Exception Handler), `security` และ `config` (JWT, Firebase, Swagger)

Design Pattern ที่ใช้
- Enterprise Pattern: Layered Architecture, MVC, Repository, Service Layer, DTO + Mapper, Dependency Injection
- GoF (กลุ่ม Behavioral): State (สถานะของ Report), Observer (แจ้งเตือนผู้ติดตามเมื่อสถานะเปลี่ยน), Strategy (ตรวจสิทธิ์การยื่นเคลมตามประเภทประกาศ LOST/FOUND)

รายละเอียดดูที่ [doc/design-patterns.md](doc/design-patterns.md) และ [doc/solid-analysis.md](doc/solid-analysis.md)

![Component Diagram](doc/diagrams/DiagramPNG/Component%20Diagram.png)

แผนภาพอื่น ๆ (Use Case, Domain Model, Class, Sequence, Activity, State ของ Report และ Claim, Deployment) อยู่ใน [doc/diagrams/](doc/diagrams/) (ไฟล์ PDF และ PNG ใน `DiagramPNG/`)

---

## Database Design (ER Diagram)
ฐานข้อมูลมี 12 ตาราง ได้แก่ users, user_profiles, categories, tags, reports, report_images, report_status_logs, report_watchers, claims, report_tags, notifications และ stored_files

- ใช้กลยุทธ์ Hybrid ID: ตารางที่ผู้ใช้เข้าถึงผ่าน URL โดยตรง (users, reports, claims ฯลฯ) ใช้ UUID ส่วนตารางข้อมูลหลักและตารางภายในใช้ BIGINT
- ความสัมพันธ์ One-to-One (users - user_profiles), One-to-Many (เช่น users - reports, reports - claims) และ Many-to-Many (reports - tags ผ่าน report_tags)
- มี Foreign Key, Index บนคอลัมน์ที่ค้นหาบ่อย และ Partial Unique Index (`idx_unique_pending_claim`) กันการยื่นเคลมสถานะ PENDING ซ้ำบนประกาศเดียวกัน
- โครงสร้างฐานข้อมูลถูกกำหนดจาก `code/src/main/resources/schema.sql` (Hibernate ตั้งเป็น `validate`)

![ER Diagram](doc/diagrams/DiagramPNG/ER%20Diagram.png)

ดู Data Dictionary ที่ [doc/database_dictionary.md](doc/database_dictionary.md)

---

## Installation & Setup

**สิ่งที่ต้องติดตั้งก่อน**
- JDK 17 ขึ้นไป และ Maven 3.9 ขึ้นไป
- Node.js 20 ขึ้นไป (สำหรับ Frontend)
- Docker Desktop (สำหรับ Docker Compose, PostgreSQL ในเครื่อง และ Integration Test)

```bash
# Clone repository
git clone https://github.com/LynxelSins/CP353002-Lost-and-Found.git
cd CP353002-Lost-and-Found
```

**Backend**
- Profile เริ่มต้นคือ `neon` (กำหนดใน `code/src/main/resources/application.properties`) ค่าเชื่อมต่อฐานข้อมูล Neon Cloud อยู่ใน `application-neon.yml` จึงรันได้ทันทีโดยไม่ต้องตั้งค่าเพิ่ม
- หากต้องการใช้ PostgreSQL ในเครื่อง ให้เปิดฐานข้อมูลด้วย Docker แล้วรันด้วย profile `dev` (ค่าเชื่อมต่ออยู่ใน `application-dev.yml`)
  ```bash
  docker compose up -d postgres
  cd code
  mvn spring-boot:run -Dspring-boot.run.profiles=dev
  ```
- ตอนเริ่มระบบ Spring จะรัน `schema.sql` และถ้าตาราง users ยังว่างจะโหลดข้อมูลตัวอย่างจาก `seed-data.sql` ให้อัตโนมัติ
- Firebase Admin SDK อ่านไฟล์ `code/src/main/resources/firebase-service-account.json` (ดูหัวข้อ หมายเหตุเรื่อง Credentials ด้านล่าง)
- ตัวแปร Environment ที่ override ได้ (ไม่บังคับ)

| ตัวแปร | ความหมาย | ค่าเริ่มต้น |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Profile ที่ใช้ (`neon` หรือ `dev`) | `neon` |
| `PORT` | พอร์ตของ Backend | `8080` |
| `JWT_SECRET` | คีย์ลับสำหรับเซ็น JWT (ควรตั้งเองบนระบบที่ Deploy) | ค่า dev ในไฟล์ตั้งค่า |
| `JWT_EXPIRATION_MS` | อายุ Token (มิลลิวินาที) | `86400000` |
| `FIREBASE_SERVICE_ACCOUNT_PATH` | ที่อยู่ไฟล์ Firebase service account | `firebase-service-account.json` |
| `APP_UPLOAD_DIR` | โฟลเดอร์เก็บไฟล์อัปโหลด | `uploads` |

**Frontend**
- ตั้งค่า Firebase (Google Login) ที่ `code/frontend/src/firebase.js`
- ตัวแปร `VITE_API_BASE_URL` คือ URL ของ Backend (ค่าเริ่มต้น `http://localhost:8080`) หากต้องการเปลี่ยนให้สร้างไฟล์ `code/frontend/.env` เช่น
  ```
  VITE_API_BASE_URL=http://localhost:8080
  ```

---

## How to Run

### แบบรันทีละส่วน (Local Development)

เปิดใช้งาน 2 Terminal

**Terminal ที่ 1 Backend:**
```bash
cd code
mvn spring-boot:run
```
Backend จะรันที่ `http://localhost:8080` (เชื่อมต่อ Neon Cloud ตาม profile เริ่มต้น หากต้องการฐานข้อมูลในเครื่องให้ดูหัวข้อ Installation & Setup)

**Terminal ที่ 2 Frontend:**
```bash
cd code/frontend
npm install
npm run dev
```
Frontend จะรันที่ `http://localhost:5173`

### แบบรันทั้งระบบ (Docker Compose)
```bash
docker compose up --build
```
รัน Backend (`:8080`), Frontend (`:5173`), PostgreSQL (`:5432`) และ pgAdmin (`:5050`) พร้อมกัน โดย Backend ใน Compose ใช้ profile `neon` (เชื่อมต่อ Neon Cloud) เป็นค่าเริ่มต้น ส่วน PostgreSQL และ pgAdmin ในเครื่องเปิดไว้ให้ใช้เมื่อสลับเป็น profile `dev` (ดูตัวอย่างที่คอมเมนต์ไว้ใน `docker-compose.yml`)

ปิดด้วย
```bash
docker compose down
```

---

## API Documentation
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs (JSON):** `http://localhost:8080/api-docs`
- Endpoint ทั้งหมดอยู่ภายใต้ `/api/v1` (เช่น `/api/v1/reports`, `/api/v1/claims`, `/api/v1/auth`)
- Endpoint ที่ต้องยืนยันตัวตนให้เรียก `/api/v1/auth/login` เพื่อรับ JWT แล้วกด Authorize ใน Swagger UI

**รายการ Endpoint หลัก (ตรงกับ Controller ในโค้ด)**

| กลุ่ม | Method และ Path |
| :--- | :--- |
| Auth | `POST /auth/register`, `POST /auth/login`, `POST /auth/google` |
| User | `GET /users/me`, `PATCH /users/me`, `PATCH /users/me/password`, `DELETE /users/me/password` |
| Report | `POST /reports`, `GET /reports`, `GET /reports/{id}`, `GET /reports/mine`, `GET /reports/watched`, `POST /reports/{id}/close` |
| Watch | `POST /reports/{id}/watch`, `DELETE /reports/{id}/watch` |
| Claim | `POST /reports/{id}/claims`, `GET /reports/{id}/claims`, `GET /claims/mine`, `PATCH /claims/{id}/approve`, `PATCH /claims/{id}/reject` |
| Category | `GET /categories`, `GET /categories/{id}`, `POST /categories`, `PUT /categories/{id}`, `DELETE /categories/{id}` |
| Tag | `GET /tags`, `GET /tags/{id}`, `POST /tags`, `PUT /tags/{id}`, `DELETE /tags/{id}` |
| Notification | `GET /notifications`, `GET /notifications/unread-count`, `PATCH /notifications/{id}/read`, `PATCH /notifications/read-all` |
| File | `POST /uploads`, `GET /files/{id}` |
| Admin (Staff) | `DELETE /admin/reports/{id}` |

(ทุก Path ขึ้นต้นด้วย `/api/v1`)

---

## How to Run Tests
โค้ดทดสอบอยู่ที่ `code/src/test/` ส่วนรายงานผลการทดสอบและภาพประกอบอยู่ที่ [test/](test/) (ดู [test/test-report.md](test/test-report.md))

**Unit Test (JUnit 5 + Mockito)** ไม่ต้องใช้ Docker
```bash
cd code
mvn test
```

**Integration Test (Testcontainers + PostgreSQL จริง)** ต้องเปิด Docker ไว้ และถูกข้ามในการรัน `mvn test` ปกติ ต้องรันแยก
```bash
cd code
mvn test -Dtest=ClaimFlowIntegrationTest -DexcludeIntegrationTests=
```

**สรุปผลการทดสอบ** (รายละเอียดเต็มใน [test/test-report.md](test/test-report.md))

| ประเภท | จำนวนกรณี | ผ่าน |
| :--- | :---: | :---: |
| Unit Test (`mvn test`) | 116 | 116 |
| Integration Test (Testcontainers) | 5 | 5 |
| API Test (Swagger UI) | 22 | 22 |
| Authentication/Authorization Test | 5 | 5 |
| Frontend Functional Test | 6 | 6 |

Code Coverage (JaCoCo): Instruction 63%, Branch 60%, Line 62%, Method 61%, Class 82%

**ผลการทดสอบ**
- รายงานจาก Surefire: `code/target/surefire-reports/`
- รายงาน Code Coverage (JaCoCo): `code/target/site/jacoco/index.html` (สร้างหลังรัน `mvn test`)

---

## Deployment URL

**หมายเหตุ:** ระบบ deploy บน Render แผน Free ซึ่ง service จะหลับเมื่อไม่มีคนใช้งาน การเปิดครั้งแรกอาจใช้เวลา 50 วินาทีขึ้นไป

### วิธีรันเว็บที่ deploy
1. ปลุกฝั่ง Backend ก่อน โดยเปิดลิงก์ [`https://lostandfound-backend-wp7i.onrender.com/swagger-ui/index.html`](https://lostandfound-backend-wp7i.onrender.com/swagger-ui/index.html) รอจนหน้า Swagger โหลดขึ้น (Render ต้องปลุก Backend ให้ตื่นก่อน)
2. เปิดเว็บหลัก (Frontend) ที่ลิงก์ [`https://cp353002-lost-and-found.onrender.com/items`](https://cp353002-lost-and-found.onrender.com/items)
3. เข้าสู่ระบบ
   - **ผู้ใช้ทั่วไป:** เข้าสู่ระบบด้วยอีเมลของตัวเองได้เลย
   - **เจ้าหน้าที่ (Staff):** ใช้บัญชีทดสอบ
     - อีเมล: `staff@gmail.com`
     - รหัสผ่าน: `12345678`

**หมายเหตุเพิ่มเติม:** รูปภาพที่อัปโหลดอาจหายไปเมื่อ Backend ถูก restart หรือ deploy ใหม่ เพราะแผน Free ไม่มีที่เก็บไฟล์ถาวร

- **Production URL (Frontend):** `https://cp353002-lost-and-found.onrender.com/items`
- **Swagger UI (Backend, Cloud):** `https://lostandfound-backend-wp7i.onrender.com/swagger-ui/index.html`

การ Deploy อัตโนมัติ: เมื่อมีการ push เข้า `main` GitHub Actions (`.github/workflows/cd.yml`) จะเรียก Deploy Hook ของ Render ส่วน `.github/workflows/maven.yml` ทำหน้าที่ Build และรัน Unit Test (`mvn test`) เมื่อ push หรือเปิด Pull Request เข้า `develop` และ `main` โดยขั้นตอนรันเทสตั้งเป็น `continue-on-error` ผลเทสจึงแสดงเป็นรายงานแต่ไม่ทำให้ Pipeline ล้มเหลว และ CI ไม่รัน Integration Test (ต้องใช้ Docker ให้รันเองตามหัวข้อ How to Run Tests)

---

## Project Structure
```text
.
├── code/                           # Source code และ Configuration
│   ├── src/main/java/com/example/lostandfound/
│   │   ├── common/                 # AuditableEntity, ImmutableEntity (Base class)
│   │   ├── config/                 # SecurityConfig, OpenApiConfig, FirebaseConfig, WebConfig, PasswordEncoderConfig, DataSeeder
│   │   ├── controller/api/         # REST Controllers
│   │   ├── domain/entity/          # JPA Entities
│   │   ├── domain/enums/           # Enums
│   │   ├── dto/request/            # Request DTOs
│   │   ├── dto/response/           # Response DTOs
│   │   ├── event/                  # Observer Pattern (ReportStatusChangedEvent, ReportStatusEventListener)
│   │   ├── exception/              # Global Exception Handler
│   │   ├── mapper/                 # Entity <-> DTO Mapper
│   │   ├── repository/             # Spring Data JPA Repository
│   │   ├── security/               # JwtUtil, JWT Filter, UserPrincipal, RestAuthenticationEntryPoint, RestAccessDeniedHandler
│   │   ├── service/                # Service interface + ReportStatusChanger
│   │   │   ├── impl/               # Service implementation
│   │   │   ├── state/              # State Pattern (ReportState และ Open/MatchPending/Claimed/Closed/Rejected)
│   │   │   └── strategy/           # Strategy Pattern (ClaimEligibilityStrategy, Lost/Found, Resolver)
│   │   └── LostAndFoundApplication.java
│   ├── src/main/resources/         # schema.sql, seed-data.sql, application.properties, application-dev.yml, application-neon.yml, firebase-service-account.json
│   ├── src/test/
│   │   ├── java/com/example/lostandfound/
│   │   │   ├── controller/api/     # Controller Test (Admin, Auth, Category, Claim, FileUpload, Notification, Report, Tag, User)
│   │   │   ├── service/            # ReportStatusChangerTest
│   │   │   │   ├── impl/           # Service Test (Auth, Category, Claim, Notification, Report, Tag, User)
│   │   │   │   └── strategy/       # ClaimEligibilityStrategyTest
│   │   │   ├── mapper/             # ClaimMapperTest
│   │   │   ├── integration/        # ClaimFlowIntegrationTest (Testcontainers)
│   │   │   ├── AbstractIntegrationTest.java
│   │   │   └── LostAndFoundApplicationTest.java
│   │   └── resources/              # application-test.properties
│   ├── frontend/                   # React (Vite) Frontend
│   │   ├── src/
│   │   │   ├── api/                # เรียก Backend (client.js, authApi, reportApi, claimApi ฯลฯ)
│   │   │   ├── components/         # UI Components (Sidebar, ItemCard, FilterBar, ClaimModel, NotificationBell ฯลฯ)
│   │   │   ├── pages/              # หน้าเว็บ (Auth, ItemList, ItemDetail, CreateReport, MyItems, Profile)
│   │   │   ├── hooks/              # Custom Hook (useFormValidation)
│   │   │   ├── firebase.js         # ตั้งค่า Firebase (Google Login)
│   │   │   └── App.jsx, main.jsx
│   │   ├── Dockerfile, nginx.conf, vite.config.js, package.json
│   │   └── firebase-config.js
│   ├── Dockerfile
│   └── pom.xml
├── test/                           # รายงานผลการทดสอบ
│   ├── test-report.md
│   ├── images/                     # ภาพประกอบผลการรันเทส
│   └── ภาพเพิ่มเติม API Test.pdf
├── doc/                            # เอกสารทั้งหมดและสไลด์
│   ├── diagrams/                   # Use Case, Domain Model, Class, ER, Sequence, Activity, State, Component, Deployment (PDF)
│   │   └── DiagramPNG/             # แผนภาพเดียวกันในรูปแบบ PNG (State แยก Report Status และ Claim Status)
│   ├── slide/                      # สไลด์นำเสนอ (outline.md)
│   ├── taskDetail/                 # ใบงานและเอกสารประกอบการทำงาน
│   ├── solid-analysis.md
│   ├── design-patterns.md
│   ├── database_dictionary.md
│   └── รายงาน-Lost-and-Found.pdf   # เล่มรายงาน
├── img/                            # ภาพดีไซน์หน้าเว็บ
├── .github/workflows/              # CI/CD (maven.yml, cd.yml)
├── docker-compose.yml              # Full stack (frontend + backend + postgres + pgadmin)
├── package.json, package-lock.json, neon.ts   # ไฟล์ตั้งค่าที่ root (Neon / Firebase dependency)
└── .oxlintrc.json                  # ตั้งค่า Oxlint
```

---

## หมายเหตุเรื่อง Credentials

เพื่อให้ clone แล้วรันได้ทันที ทีมตั้งใจเก็บ Firebase service account
(firebase-service-account.json) และ connection ของ Neon (application-neon.yml)
ไว้ใน repo นี้ โดยเป็น credential ของโปรเจคนี้โดยเฉพาะ ใช้เพื่อการเรียนเท่านั้น
และไม่มีข้อมูลจริงของผู้ใช้ ทีมจะ rotate/ลบ credential เหล่านี้หลังจบการประเมินผล
ห้ามนำไปใช้ใน production จริง
