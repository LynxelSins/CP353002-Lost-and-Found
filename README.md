# ระบบส่งคืนของหาย Lost and Found (กลุ่มที่ 12)
ระบบแจ้งของหายและติดตามการส่งคืนของที่พบ (Traffy Fondue Style) พัฒนาด้วย Spring Boot 3.x ตามสถาปัตยกรรมแบบ Layered Architecture พร้อมเชื่อมต่อกับฐานข้อมูล PostgreSQL บน Neon Cloud เพื่อให้การติดตามสถานะสิ่งของ การพิสูจน์ความเป็นเจ้าของ และการส่งมอบของคืนเป็นไปอย่างมีประสิทธิภาพ โปร่งใส และตรวจสอบได้จริง

## สมาชิกกลุ่ม (กลุ่มที่ 12)
| ลำดับ | ชื่อ-นามสกุล | รหัสนักศึกษา | Email | Section | Branch| หน้าที่รับผิดชอบ |
| :---: | :--- | :---: | :--- | :---: | :--- | :--- |
| 1 | นางสาวณัฐนันทน์ บุษดี| 673380037-1 | natthanan.bo@kkumail.com | 01 | `natthanan_6733800371_01` | **Backend Data Layer & Document Master:** Database Schema, Entity, Spring Data JPA Repository, คุมภาพรวมเอกสารและ Diagrams |
| 2 | นางสาวศิริรัตน์ ชัยชนะ| 673380060-6 | sirirat.chai@kkumail.com | 01 | `sirirat_6733800606_01` | **Core Backend Logic & API Service:** REST Controllers, Service Layer, DTO/Mapper, Validation, Swagger/OpenAPI |
| 3 | นางสาวปภาวรินทร์ นาเมืองรักษ์| 673380275-5 | phapawarin.n@kkumail.com | 02 | `phapawarin_6733802755_02` | **System Architecture & Cloud/DevOps:** System Boilerplate, Neon Cloud DB, Dockerfile, docker-compose.yml, Cloud Deployment & CI/CD |
| 4 | นางสาวปรนันท์ บุสดีวงค์| 673380276-3 | poranun.b@kkumail.com | 02 | `poranun_6733802763_02` | **UI/UX Assistant & QA Specialist:** Frontend UI Layout/Spacing, API & System Testing, JUnit 5/Mockito Tests, Demo & Presentation |
| 5 | นางสาววิภาวี ฤทธิหาญ| 673380514-3 | wiphawi_ri@kkumail.com | 01 | `wiphawi_6733805143_01` | **Frontend Lead Developer:** Core Web UI Components, Page Flow, Layout, State Management & API Integration |

> ⚠️ **คำเตือนเรื่อง Branch:** รูปแบบชื่อ branch ต้องเป็น `ชื่อจริง_รหัสนักศึกษา_section` ตามตัวอย่างในตารางเท่านั้น (ห้ามผิดรูปแบบเด็ดขาดเพื่อป้องกันการถูกหัก 5 คะแนน)

---

## Tech Stack
- **Backend:** Spring Boot 3.x (Java 17+)
- **Build Tool:** Maven
- **Database:** PostgreSQL (Neon Cloud)
- **ORM:** Spring Data JPA (Hibernate)
- **Authentication:** Spring Security + JWT, Firebase Authentication (Google Login)
- **API Documentation:** Swagger / OpenAPI (Springdoc)
- **Frontend:** React (Vite)

---

## System Architecture
อธิบายสถาปัตยกรรม Layered Architecture ของระบบ:
- **Presentation Layer:** Controller / RestController / Views
- **Service Layer:** Business Logic & Transactions
- **Repository Layer:** Data Access Layer (Spring Data JPA)
- **Domain / Entity:** Entities, Value Objects, Enums & DTOs

---

## Database Design (ER Diagram)
ใส่รูปภาพหรือลิงก์ไปยัง ER Diagram:
- ดูรายละเอียดใน [doc/diagrams/](doc/diagrams/) และเอกสาร Data Dictionary ใน [doc/](doc/)

---

## Installation & Setup
ขั้นตอนการติดตั้งและตั้งค่า Environment:
```bash
# Clone repository
git clone https://github.com/LynxelSins/CP353002-Lost-and-Found.git
cd CP353002-Lost-and-Found
```

**Backend:** ตั้งค่า Environment Variable สำหรับเชื่อมต่อฐานข้อมูล Neon Cloud ก่อนรัน (ดูตัวแปรที่ต้องตั้งใน `code/src/main/resources/application-neon.yml`) หรือใช้ `application-dev.yml` เพื่อรันกับ PostgreSQL บนเครื่องตัวเองผ่าน Docker แทน

**Frontend:** ตั้งค่า Firebase ใน `code/frontend/firebase-config.js` และตัวแปร `VITE_API_BASE_URL` ให้ชี้ไปที่ Backend (ดีฟอลต์ `http://localhost:8080`)

ตั้งค่าฐานข้อมูลใน `code/src/main/resources/application.properties` หรือ `application.yml`

---

## How to Run

### แบบรันทีละส่วน (Local Development)

เปิดใช้งาน 2 Terminal

**Terminal ที่ 1 Backend:**
```bash
cd code
mvn spring-boot:run
```
Backend จะรันที่ `http://localhost:8080`

**Terminal ที่ 2 Frontend:**
```bash
cd code
cd frontend
npm install
npm run dev
```
Frontend จะรันที่ `http://localhost:5173`

### แบบรันทั้งระบบพร้อมฐานข้อมูล (Docker Compose)
```bash
docker compose up --build
```
รัน Backend (`:8080`), Frontend (`:5173`), PostgreSQL (`:5432`) และ pgAdmin (`:5050`) พร้อมกันทั้งหมด

ปิดด้วย
```bash
docker compose down
```

---

## API Documentation
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs:** `http://localhost:8080/v3/api-docs`

---

## How to Run Tests
คำสั่งสำหรับรัน Unit Test และ Integration Test:
```bash
cd code
mvn test
```
ผลการทดสอบจะอยู่ที่ `code/target/surefire-reports/`

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

---

## Project Structure
```text
.
├── code/                       # Source code และ Configuration
│   ├── src/main/java/com/example/lostandfound/
│   │   ├── common/             # AuditableEntity, ImmutableEntity (Base class)
│   │   ├── config/             # Security, Swagger, Firebase, DataSeeder
│   │   ├── controller/api/     # REST Controllers
│   │   ├── domain/entity/      # JPA Entities
│   │   ├── domain/enums/       # Enums
│   │   ├── dto/request/        # Request DTOs
│   │   ├── dto/response/       # Response DTOs
│   │   ├── event/              # Observer Pattern (ReportStatusChangedEvent)
│   │   ├── exception/          # Global Exception Handler
│   │   ├── mapper/             # Entity <-> DTO Mapper
│   │   ├── repository/         # Spring Data JPA Repository
│   │   ├── security/           # JWT Filter, UserPrincipal
│   │   ├── service/            # Service interface + ReportStatusChanger
│   │   │   ├── impl/           # Service implementation
│   │   │   ├── state/          # State Pattern (ReportState)
│   │   │   └── strategy/       # Strategy Pattern (ClaimEligibilityStrategy)
│   │   └── LostAndFoundApplication.java
│   ├── src/main/resources/     # schema.sql, seed-data.sql, application*.yml
│   ├── src/test/               # Unit/Integration Tests
│   ├── frontend/               # React (Vite) Frontend
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml          # Full stack (frontend + backend + postgres + pgadmin)
├── doc/                        # เอกสารทั้งหมด
│   ├── diagrams/                # Use Case, Class, ER, Sequence ฯลฯ
│   ├── slide/
│   ├── solid-analysis.md
│   ├── design-patterns.md
│   ├── database_dictionary.md
│   └── test-report.md
└── img/                         # ไฟล์มัลติมีเดียประกอบ
```

---

## หมายเหตุเรื่อง Credentials

เพื่อให้ clone แล้วรันได้ทันที ทีมตั้งใจเก็บ Firebase service account
(firebase-service-account.json) และ connection ของ Neon (application-neon.yml)
ไว้ใน repo นี้ โดยเป็น credential ของโปรเจคนี้โดยเฉพาะ ใช้เพื่อการเรียนเท่านั้น
และไม่มีข้อมูลจริงของผู้ใช้ ทีมจะ rotate/ลบ credential เหล่านี้หลังจบการประเมินผล
ห้ามนำไปใช้ใน production จริง
