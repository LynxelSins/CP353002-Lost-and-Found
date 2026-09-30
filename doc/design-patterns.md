# Design Patterns

## Enterprise / Architectural Patterns
| Pattern | ใช้ที่ไหน |
|---|---|
| Layered Architecture | โครงสร้าง package ทั้งโปรเจกต์ (controller → service → repository → domain) |
| MVC | `controller/api/` (Controller) + Entity/DTO (Model) + JSON Response (View) |
| Repository Pattern | ทุก interface ใน `repository/` ที่ extends `JpaRepository` |
| Service Layer Pattern | `service/` + `service/impl/` รวม Business Logic |
| DTO Pattern + Mapper | `dto/request/`, `dto/response/`, `mapper/` แยก Entity ออกจาก API Contract |
| Dependency Injection | Constructor Injection ทุกจุด (`@RequiredArgsConstructor`) |

## GoF Patterns (กลุ่ม Behavioral — 3 แบบ)

| Pattern | ปัญหาที่แก้ | ไฟล์/คลาสที่ใช้ | Diagram อ้างอิง |
|---|---|---|---|
| **State** | ควบคุมการเปลี่ยน `Report.status` ให้ถูกต้องตาม workflow (OPEN → MATCH_PENDING → CLAIMED → CLOSED หรือ REJECTED) โดยไม่ใช้ if-else สะสมใน Service | `service/ReportStatusChanger.java`, `service/state/ReportState.java` (interface) + `OpenState`, `MatchPendingState`, `ClaimedState`, `ClosedState`, `RejectedState` | `doc/diagrams/class-diagram-with-patterns.md`, `doc/diagrams/State Diagram` |
| **Observer** | แจ้งเตือนผู้ติดตาม (`report_watchers`) อัตโนมัติทุกครั้งที่สถานะ `Report` เปลี่ยน โดยไม่ให้ `ReportStatusChanger` ต้อง couple กับ logic แจ้งเตือนตรงๆ | `event/ReportStatusChangedEvent.java`, `event/ReportStatusEventListener.java` (ใช้ Spring `ApplicationEventPublisher` + `@EventListener`), `service/NotificationService.java` | `doc/diagrams/class-diagram-with-patterns.md` |
| **Strategy** | ตรวจสิทธิ์การยื่นเคลม (Claim Eligibility) ที่กติกาต่างกันตาม `ReportType` (LOST vs FOUND) โดยไม่ต้อง if-else ใน `ClaimServiceImpl` — เพิ่มกติกาใหม่ทำได้แค่เพิ่มคลาส (Open/Closed Principle) | `service/strategy/ClaimEligibilityStrategy.java` (interface), `LostReportClaimEligibilityStrategy.java`, `FoundReportClaimEligibilityStrategy.java`, `ClaimEligibilityStrategyResolver.java` | `doc/diagrams/class-diagram-with-patterns.md` |
