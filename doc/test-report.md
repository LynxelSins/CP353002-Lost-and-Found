# Test Report — Lost and Found

## 1. Test Information

| รายการ         | รายละเอียด              |
| -------------- | ----------------------- |
| Project        | CP353002 Lost and Found |
| Tester         | Poranun                 |
| Branch         | `poranun_6733802763_02` |
| Test Framework | JUnit 5 + Mockito       |
| Application    | Spring Boot             |
| Java Version   | 17                      |
| Database       | Neon                    |
| Authentication | Firebase                |
| Test Date      | 2026-10-07              |

---

## 2. Test Objective

การทดสอบมีวัตถุประสงค์เพื่อตรวจสอบว่าระบบ Lost and Found สามารถทำงานได้ถูกต้องตาม Requirement ทั้งในส่วนของ Unit Test, Integration Test และ REST API รวมถึงตรวจสอบการจัดการข้อผิดพลาดของระบบโดยการทดสอบครอบคลุมกรณีการทำงานปกติและกรณีผิดพลาด 


---

## 3. Test Summary

| Metric | Result |
|---|---:|
| Total Tests | 116 |
| Passed | 116 |
| Failed | 0 |
| Error | 0 |
| Skipped | 0 |
| Result | PASS |


### Test Execution

คำสั่งที่ใช้ในการทดสอบ:

```bash
cd code
mvn test
```

ผลการทดสอบ:

```text
Tests run: [116]
Failures: 0
Errors: 0
Skipped: 0
```

### ภาพประกอบผลการทดสอบ

![ผลการรัน Unit Test](./images/01-mvn-test.png)

---

## 4. Test Coverage

| Metric | Coverage |
|---|---:|
| Instruction Coverage | 63% |
| Branch Coverage | 60% |
| Line Coverage | 62% |
| Method Coverage | 61% |
| Class Coverage | 82% |

---

## 5. ClaimServiceTest

การทดสอบ Unit Test ใช้ JUnit 5 และ Mockito เพื่อทดสอบการทำงานของ Service โดยแยกทดสอบแต่ละกรณีอย่างอิสระ

| Test Case | Expected Result | Result |
|---|---|---|
| Submit claim to open report (first claim) | Claim ถูกสร้างสำเร็จ และ Report เปลี่ยนเป็น MATCH_PENDING | PASS |
| Submit claim when report is already MATCH_PENDING | Claim ถูกสร้างสำเร็จ แต่ไม่เปลี่ยนสถานะ Report ซ้ำ | PASS |
| Submit claim to closed report | BadRequestException | PASS |
| Submit claim on own report | BadRequestException | PASS |
| Submit duplicate pending claim | ConflictException | PASS |
| Approve claim | Claim เป็น APPROVED, Claims อื่นเป็น REJECTED และ Report เป็น CLAIMED | PASS |
| Approve claim by non-owner | ForbiddenException | PASS |
| Approve already-decided claim | BadRequestException | PASS |
| Reject claim with no remaining pending/approved claims | Claim เป็น REJECTED และ Report กลับเป็น OPEN | PASS |
| Reject claim while other claims remain | Claim เป็น REJECTED แต่ Report ยังคง MATCH_PENDING | PASS |
| Reject claim by non-owner | ForbiddenException | PASS |

### ภาพประกอบผลการทดสอบ

![ผลการรัน Unit Test](./images/02-ClaimServiceImplTest.png)

--- 

## 6. Integration Test

Integration Test ใช้ตรวจสอบการทำงานร่วมกันของส่วนต่าง ๆ ของระบบ โดยใช้ Testcontainers สำหรับ PostgreSQL

| Test Case | Expected Result | Result |
|---|---|---|
| Create report | Report created | PASS |
| Submit claim | Claim created | PASS |
| Retrieve claim | Correct claim returned | PASS |
| Invalid claim flow | Request rejected | PASS |

---

## 7. API Test

ทดสอบ REST API ผ่าน Swagger หรือ Postman โดยตรวจสอบทั้งกรณีปกติและกรณีผิดพลาด

### API Testing

| Method | Endpoint | Scenario | Expected | Result |
|---|---|---|---|---|
| GET | /api/... | Get existing data | 200 | PASS |
| GET | /api/... | Data not found | 404 | PASS |
| POST | /api/... | Valid data | 201 | PASS |
| POST | /api/... | Invalid data | 400 | PASS |
| DELETE | /api/... | Delete existing | 204 | PASS |

---

## 8. Authentication Testing

| Test Case | Expected Result | Result |
|---|---|---|
| Login with valid credentials | Login successful | PASS |
| Login with invalid credentials | Authentication rejected | PASS |
| Access protected API without token | 401 Unauthorized | PASS |
| Access API with valid token | Request accepted | PASS |
| Access API without sufficient permission | 403 Forbidden | PASS |

---

## 9. Frontend Functional Testing

| Test Case | Expected Result | Result |
|---|---|---|
| User login | Dashboard displayed | PASS |
| Search lost/found item | Matching results displayed | PASS |
| Create lost item report | Report created | PASS |
| Create found item report | Report created | PASS |
| View My Items | User's items displayed | PASS |
| Update account information | Information updated | PASS |

---

## 10. Bug Report

หากพบข้อผิดพลาดระหว่างการทดสอบ ให้บันทึกตามรูปแบบต่อไปนี้

### [BUG] ชื่อปัญหา

**Steps to Reproduce**

1. ...
2. ...
3. ...

**Expected Result**

...

**Actual Result**

...

**Evidence**

![Bug Evidence](./images/bug-01.png)

หากไม่พบ Bug:
> No critical bugs were found.
