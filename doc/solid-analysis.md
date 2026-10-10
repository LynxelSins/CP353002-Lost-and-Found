# SOLID Analysis

## S — Single Responsibility
- `service/ReportStatusChanger.java` (บรรทัด 30-48) — รวมเฉพาะ logic การเปลี่ยนสถานะ Report + บันทึก log + publish event เท่านั้น ไม่ปน CRUD ทั่วไป (แยกจาก ReportServiceImpl)
- `service/FileStorageService.java` (บรรทัด 7-8) / `service/impl/FileStorageServiceImpl.java` (บรรทัด 17-49) — รับผิดชอบแค่เก็บ/ดึงไฟล์ (`store()`/`load()`) แยกออกจาก `FileUploadController` อย่างชัดเจน
- `common/AuditableEntity.java` (บรรทัด 15-23) / `common/ImmutableEntity.java` (บรรทัด 14-18) — รับผิดชอบแค่เรื่อง audit timestamp แยกจาก business field ของแต่ละ Entity
- `domain/entity/` ทุก Entity — เก็บแค่ data mapping ไม่มี business logic ปนอยู่ในคลาส

## O — Open/Closed
- `service/state/ReportState.java` (บรรทัด 9) interface + concrete class `service/state/OpenState.java` (บรรทัด 9-22), `MatchPendingState.java`, `ClaimedState.java`, `ClosedState.java`, `RejectedState.java` — เพิ่มสถานะใหม่ทำได้แค่เพิ่มคลาส ไม่ต้องแก้ if-else ใน `ReportStatusChanger`
- `service/strategy/ClaimEligibilityStrategy.java` (บรรทัด 12) + `service/strategy/ClaimEligibilityStrategyResolver.java` (บรรทัด 13) — เพิ่มกฎเคลมสำหรับ ReportType ใหม่ทำได้แค่เพิ่ม Strategy class ใหม่ ไม่ต้องแก้ของเดิม

## L — Liskov Substitution
- `service/state/OpenState.java` (บรรทัด 15-22) และ concrete `ReportState` ทุกตัว implement `canTransitionTo()`/`getStatus()` ตาม interface ได้ครบ ไม่มีตัวไหน throw `UnsupportedOperationException`
- Entity ทุกตัวที่ extends `AuditableEntity` (บรรทัด 15) / `ImmutableEntity` (บรรทัด 14) ใช้แทน superclass ได้ปกติทุกจุดที่เรียก `getCreatedAt()`

## I — Interface Segregation
- `service/state/ReportState.java` (บรรทัด 9-13) — มีแค่ `getStatus()`/`canTransitionTo()` แยกออกจาก `ReportService`/`ClaimService` ไม่รวมเป็น interface ใหญ่ตัวเดียว
- Repository แยกตาม Entity: `repository/ReportRepository.java` (บรรทัด 15), `repository/ClaimRepository.java` (บรรทัด 12), `repository/TagRepository.java` (บรรทัด 8), `repository/StoredFileRepository.java` (บรรทัด 8) แทนที่จะมี Repository รวมทุก query ไว้ที่เดียว
- `service/FileStorageService.java` (บรรทัด 7-9) มีแค่ 2 method (`store`/`load`) ที่ `FileUploadController` ใช้จริง ไม่มี method เกินความจำเป็น

## D — Dependency Inversion
- `service/ReportStatusChanger.java` (บรรทัด 30-31), `service/impl/ClaimServiceImpl.java` (บรรทัด 34-44), `controller/api/FileUploadController.java` (บรรทัด 26-29) ใช้ `@RequiredArgsConstructor` รับ dependency ผ่าน Constructor Injection ทั้งหมด ไม่มี field injection
- `ClaimServiceImpl` (บรรทัด 38-44) พึ่งพา interface (`ClaimRepository`, `ReportRepository`, `UserRepository`, `NotificationService`, `ReportStatusChanger`, `ClaimMapper`, `ClaimEligibilityStrategyResolver`) ไม่ผูกกับ concrete class — `FileUploadController` (บรรทัด 29) รู้จักแค่ `FileStorageService` (interface) ไม่รู้จัก `FileStorageServiceImpl` โดยตรง