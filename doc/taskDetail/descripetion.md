Repository: create branch for each.


### 1. รูปแบบชื่อ Branch (บังคับ ห้ามผิดรูปแบบเด็ดขาด ⚠️)
- เราจะไม่ push โค้ดตรงเข้ากิ่ง `main` กันนะ ให้ทุกคนแตก Branch ของแต่ละคนเอง
- **รูปแบบชื่อ:** `ชื่อ_รหัสนักศึกษา_section`
- **ตัวอย่าง:** `somchai_66123456_01`
- 🚨 **บทลงโทษ:**
    - **ตั้งชื่อผิดรูปแบบ = ไม่ตรวจ และถูกหัก -5 คะแนนรายบุคคล**
    - **ห้าม** ตั้งชื่อแบบ generic ทั่วไป เช่น `feature/login`, `work` (ต้องเป็นชื่อ_รหัสนักศึกษา_section ของเจ้าของงานเท่านั้น)
- ทำเสร็จแล้วเปิด Pull Request (PR) เข้ามา เดี๋ยวให้คุณ*Backend Data Layer หรือ Git Coordinator* ช่วยเช็คก่อน Merge ให้ กิ่งหลักจะได้ไม่พัง
### 2. โครงสร้าง Branch ในโปรเจกต์

อาจารย์แบ่งโครงสร้างออกเป็น 3 ระดับ:

| Branch                          | หน้าที่ / กฎการใช้งาน                                                                       |
| ------------------------------- | ------------------------------------------------------------------------------------------- |
| **`main`**                      | **Production** — รวมโค้ดที่สมบูรณ์ Merge ได้เฉพาะ Version ที่ส่งมอบเท่านั้น (ห้าม Push ตรง) |
| **`develop`**                   | **Integration** — รวมงานจากทุกคน                                                            |
| **`ชื่อ_รหัสนักศึกษา_section`** | **Branch ส่วนตัวของแต่ละคน** (ทุกคนต้องแตกกิ่งออกมาทำใน branch ของตัวเอง)                   |
### 3. กฎเหล็กและการรวมโค้ด (มีผลต่อคะแนนโดยตรง)

1. **การรวมงาน (Merge):**
    - การรวมงานจาก branch ส่วนตัวเข้า `develop` (หรือเข้า `main`) **ต้องผ่าน Pull Request (PR) เสมอ**
    - ต้องมี **Reviewer อย่างน้อย 1 คนในทีม** เข้ามากดตรวจ/รีวิวโค้ด
2. **ห้ามฝากเพื่อน Commit / Push โดยเด็ดขาด:**
    - ตรวจพบ = **ได้ 0 คะแนนในงานส่วนนั้นทันที**
    - ทุกคนต้องใช้บัญชี GitHub ของตนเอง
    - ก่อนเริ่มงาน ทุกคนต้องตั้งค่า Git config ให้ตรงกับ GitHub ของตัวเอง:
        
        bash
        
        git config user.name "ชื่อ-นามสกุลอังกฤษ"
        
        git config user.email "อีเมลที่ผูกกับGitHub"
        
3. **เกณฑ์ Commit:**
    - ทุกคนต้องมี Commit ที่มีความหมาย **ไม่น้อยกว่า 15 commits ต่อคน**
    - ต้อง **กระจายตลอดช่วงเวลาทำโปรเจกต์** (ห้ามดองแล้วมา Commit รวดเดียวก่อนส่ง)
    - การให้คะแนนรายบุคคลดูจาก `git log`, `Contributors Graph`, `Pull Request`, และ `Code Review` ใน branch ของแต่ละคน

>- การให้คะแนนรายบุคคลดูจาก `git log`, `Contributors Graph`, `Pull Request`, และ `Code Review` ใน branch ของแต่ละคน

### 4. รูปแบบ Commit Message (Convention)

อาจารย์กำหนดรูปแบบ: `<type>: <สิ่งที่ทำ>`

- `feat:` เช่น `feat: add customer registration API`
- `fix:` เช่น `fix: correct order total calculation`
- `refactor:` เช่น `refactor: extract discount strategy interface`
- `test:` เช่น `test: add unit test for OrderService`
- `docs:` เช่น `docs: update user manual`
