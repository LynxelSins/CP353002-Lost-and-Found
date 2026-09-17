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

## GoF Patterns (กลุ่ม Behavioral — เลือก 3 แบบ)
| Pattern | ปัญหาที่แก้ | ไฟล์/คลาสที่ใช้ | Diagram อ้างอิง |
|---|---|---|---|
| **State** | ควบคุมการเปลี่ยน `Report.status` ให้ถูกต้องตาม workflow ไม่ใช้ if-else สะสม | `ReportStatusChanger.java`, `service/state/ReportState.java` (interface) + `OpenState`/`MatchPendingState`/`ClaimedState`/`ClosedState`/`RejectedState` | `doc/diagrams/class-diagram-with-patterns.md`, `doc/diagrams/State Diagram` |
| **Observer** | แจ้งเตือนผู้ติดตาม (`report_watchers`) อัตโนมัติเมื่อสถานะ Report เปลี่ยน โดยไม่ couple กับโค้ดแจ้งเตือนตรงๆ | `event/ReportStatusChangedEvent.java`, `event/ReportStatusEventListener.java` (ใช้ Spring `ApplicationEventPublisher`) | `doc/diagrams/class-diagram-with-patterns.md` |
| **Strategy** | ตรวจสิทธิ์การยื่นเคลม (`ClaimEligibility`) ที่กติกาต่างกันตาม `ReportType` (LOST vs FOUND) โดยไม่ต้อง if-else ใน `ClaimServiceImpl` | `service/strategy/ClaimEligibilityStrategy.java` (interface) + `service/strategy/ClaimEligibilityStrategyResolver.java` | *(ต้องวาด Class Diagram ใหม่ให้ตรง เพราะฉบับเดิมออกแบบเป็น `ReportSearchStrategy` ซึ่งไม่ตรงกับของจริง)* |

> ⚠️ ตอนนี้ `service/state/` และ `service/strategy/` ยังไม่มีไฟล์จริงใน repo (compile error) — ตารางนี้เขียนจากคลาสที่ถูก import/เรียกใช้ในโค้ดที่มีอยู่แล้ว รอเพื่อนเพิ่มไฟล์กลับเข้ามาแล้วตรวจ path ให้ตรง