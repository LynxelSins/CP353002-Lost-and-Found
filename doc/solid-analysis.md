# SOLID Analysis

## S — Single Responsibility
- `ReportStatusChanger.java` — รวมเฉพาะ logic การเปลี่ยนสถานะ Report + บันทึก log + publish event เท่านั้น ไม่ปน CRUD ทั่วไป (แยกจาก ReportServiceImpl)
- `AuditableEntity.java` / `ImmutableEntity.java` — รับผิดชอบแค่เรื่อง audit timestamp แยกจาก business field ของแต่ละ Entity
- แต่ละ Entity (User, Report, Claim ฯลฯ) — เก็บแค่ data mapping ไม่มี business logic ปนอยู่ในคลาส

## O — Open/Closed
- `ReportState` interface + concrete class (`OpenState`, `MatchPendingState`, `ClaimedState`, `ClosedState`, `RejectedState`) — เพิ่มสถานะใหม่ทำได้แค่เพิ่มคลาส ไม่ต้องแก้ if-else ใน `ReportStatusChanger`
- `ClaimEligibilityStrategy` + `ClaimEligibilityStrategyResolver` — เพิ่มกฎเคลมสำหรับ ReportType ใหม่ทำได้แค่เพิ่ม Strategy class

## L — Liskov Substitution
- Concrete `ReportState` ทุกตัว implement `canTransitionTo()`/`getStatus()` ตาม interface ได้ครบ ไม่มีตัวไหน throw `UnsupportedOperationException`
- Entity ทุกตัวที่ extends `AuditableEntity`/`ImmutableEntity` ใช้แทน superclass ได้ปกติทุกจุดที่เรียก `getCreatedAt()`

## I — Interface Segregation
- แยก `ReportState` (มีแค่ `next()`/`canTransitionTo()`/`getStatus()`) ออกจาก `ReportService`/`ClaimService` ไม่รวมเป็น interface ใหญ่ตัวเดียว
- Repository แยกตาม Entity (`ReportRepository`, `ClaimRepository`, `TagRepository` ...) แทนที่จะมี Repository รวมทุก query ไว้ที่เดียว

## D — Dependency Inversion
- `ReportStatusChanger`, `ClaimServiceImpl` ใช้ `@RequiredArgsConstructor` รับ dependency ผ่าน Constructor Injection ทั้งหมด ไม่มี field injection (`@Autowired` บน field)
- ServiceImpl พึ่งพา interface (`ReportRepository`, `NotificationService`, `ReportSearchStrategy`/`ClaimEligibilityStrategy`) ไม่ผูกกับ concrete class ตัวใดตัวหนึ่ง

> หมายเหตุ: เลขบรรทัดที่แน่นอนให้ระบุเพิ่มหลัง merge โค้ดครบและไฟล์ `service/state/`, `service/strategy/` ถูกเพิ่มกลับเข้า repo แล้ว (ตอนนี้ยัง compile ไม่ผ่าน)