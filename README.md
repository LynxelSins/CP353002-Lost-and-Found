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

## Tech Stack
- **Backend:** Spring Boot 3.x (Java 17+)
- **Build Tool:** Maven
- **Database:** PostgreSQL / MySQL
- **ORM:** Spring Data JPA (Hibernate)
- **API Documentation:** Swagger / OpenAPI (Springdoc)
- **Frontend:** Thymeleaf / React / Vue / Angular

## System Architecture
อธิบายสถาปัตยกรรม Layered Architecture ของระบบ:
- **Presentation Layer:** Controller / RestController / Views
- **Service Layer:** Business Logic & Transactions
- **Repository Layer:** Data Access Layer (Spring Data JPA)
- **Domain / Entity:** Entities, Value Objects, Enums & DTOs

## Database Design (ER Diagram)
ใส่รูปภาพหรือลิงก์ไปยัง ER Diagram:
- ดูรายละเอียดใน [doc/diagrams/](doc/diagrams/) และเอกสาร Data Dictionary ใน [doc/](doc/)

## Installation & Setup
ขั้นตอนการติดตั้งและตั้งค่า Environment:
```bash
# Clone repository
git clone https://github.com/<username>/<repo-name>.git
cd <repo-name>
```

ตั้งค่าฐานข้อมูลใน `code/src/main/resources/application.properties` หรือ `application.yml`

## How to Run
ขั้นตอนการรัน Backend:
```bash
cd code
./mvnw spring-boot:run
```

## API Documentation
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs:** `http://localhost:8080/v3/api-docs`

## How to Run Tests
คำสั่งสำหรับรัน Unit Test และ Integration Test:
```bash
cd code
./mvnw test
```

## Deployment URL
- **Production URL:** `https://your-production-url.com`
- **Swagger UI (Cloud):** `https://your-production-url.com/swagger-ui.html`

## Project Structure
```text
.
├── code/       # Source code และ Configuration (Spring Boot)
├── test/       # การทดสอบทั้งหมด (Test reports, automated tests)
├── doc/        # เอกสารทั้งหมด, สไลด์, และ Diagrams
│   ├── diagrams/
│   ├── slide/
│   └── taskDetail/
└── img/        # ไฟล์มัลติมีเดียและรูปภาพประกอบ
```
