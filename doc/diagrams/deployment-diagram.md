<!-- ตำแหน่งที่รับผิดชอบไฟล์นี้: System Architecture & Cloud/DevOps -->
<!-- เติม: ชื่อ platform hosting จริง (Render/Railway/ฯลฯ), URL จริงหลัง deploy -->

# Deployment Diagram

<!-- คำอธิบายสั้นๆ ว่า deploy ยังไง ใช้ platform อะไร (2-3 บรรทัด) -->

```mermaid
graph TD
subgraph Browser["เครื่องผู้ใช้"]
  UI[Web Browser]
end
subgraph Cloud["Cloud Hosting"]
  App[Spring Boot App - Docker Container]
end
subgraph NeonCloud["Neon Cloud"]
  PG[(PostgreSQL Database)]
end
subgraph GoogleCloud["Google/Firebase"]
  FB[Firebase Auth Service]
end

UI -->|HTTPS| App
App -->|JDBC/SSL| PG
App -->|REST| FB
```

<!-- เปลี่ยนชื่อ "Cloud Hosting" เป็นชื่อ platform จริงที่ใช้ deploy -->
<!-- ถ้ามี CI/CD (GitHub Actions) ให้เพิ่ม subgraph แยกแสดง pipeline ด้วย -->
