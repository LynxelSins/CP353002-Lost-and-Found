-- =====================================================================
-- MOCK DATA — สอดคล้องกับ schema.sql (Hybrid ID: UUID/BIGINT)
-- รันหลัง schema.sql เสมอ (spring.jpa.hibernate.ddl-auto=validate)
-- =====================================================================

-- ---------------------------------------------------------------------
-- USERS (8 คน) -- password_hash เป็นค่า mock ไม่ใช่ BCrypt จริง (สำหรับ dev เท่านั้น)
-- ---------------------------------------------------------------------
INSERT INTO users (email, password_hash, role) VALUES
<<<<<<< HEAD
('alice@example.com', '$2a$10$mockHashAlice000000000000000000000000000000000000000', 'USER'),
('bob@example.com',   '$2a$10$mockHashBob0000000000000000000000000000000000000000',  'USER'),
('carol@example.com', '$2a$10$mockHashCarol00000000000000000000000000000000000000', 'USER'),
('dave@example.com',  '$2a$10$mockHashDave000000000000000000000000000000000000000', 'USER'),
('eve@example.com',   '$2a$10$mockHashEve00000000000000000000000000000000000000000', 'USER'),
('frank@example.com', '$2a$10$mockHashFrank00000000000000000000000000000000000000', 'USER'),
('grace@example.com', '$2a$10$mockHashGrace00000000000000000000000000000000000000', 'ADMIN'),
('heidi@example.com', '$2a$10$mockHashHeidi00000000000000000000000000000000000000', 'USER');
=======
('alice@example.com', '$2a$10$mockHashAlice000', 'USER'),
('bob@example.com',   '$2a$10$mockHashBob000',  'USER'),
('carol@example.com', '$2a$10$mockHashCarol000', 'USER'),
('dave@example.com',  '$2a$10$mockHashDave000', 'USER'),
('eve@example.com',   '$2a$10$mockHashEve000', 'USER'),
('frank@example.com', '$2a$10$mockHashFrank000', 'USER'),
('grace@example.com', '$2a$10$mockHashGrace000', 'STAFF'),
('heidi@example.com', '$2a$10$mockHashHeidi000', 'USER');
>>>>>>> ef18fe086e9a24c4ce017fa910f125807fcf614d

-- ---------------------------------------------------------------------
-- USER PROFILES (8 รายการ)
-- ---------------------------------------------------------------------
INSERT INTO user_profiles (user_id, full_name, phone_number, avatar_url)
SELECT user_id, 'Alice Johnson', '081-111-1111', 'https://i.pravatar.cc/150?u=alice' FROM users WHERE email='alice@example.com'
UNION ALL
SELECT user_id, 'Bob Smith',     '081-222-2222', 'https://i.pravatar.cc/150?u=bob'   FROM users WHERE email='bob@example.com'
UNION ALL
SELECT user_id, 'Carol White',   '081-333-3333', 'https://i.pravatar.cc/150?u=carol' FROM users WHERE email='carol@example.com'
UNION ALL
SELECT user_id, 'Dave Brown',    '081-444-4444', 'https://i.pravatar.cc/150?u=dave'  FROM users WHERE email='dave@example.com'
UNION ALL
SELECT user_id, 'Eve Davis',     '081-555-5555', 'https://i.pravatar.cc/150?u=eve'   FROM users WHERE email='eve@example.com'
UNION ALL
SELECT user_id, 'Frank Miller',  '081-666-6666', 'https://i.pravatar.cc/150?u=frank' FROM users WHERE email='frank@example.com'
UNION ALL
SELECT user_id, 'Grace Wilson',  '081-777-7777', 'https://i.pravatar.cc/150?u=grace' FROM users WHERE email='grace@example.com'
UNION ALL
SELECT user_id, 'Heidi Moore',   '081-888-8888', 'https://i.pravatar.cc/150?u=heidi' FROM users WHERE email='heidi@example.com';

-- ---------------------------------------------------------------------
-- CATEGORIES (8 หมวด)
-- ---------------------------------------------------------------------
INSERT INTO categories (category_name, description) VALUES
('Electronics',  'มือถือ แท็บเล็ต หูฟัง โน้ตบุ๊ก'),
('Documents',    'บัตรประชาชน ใบขับขี่ พาสปอร์ต'),
('Keys',         'กุญแจบ้าน กุญแจรถ'),
('Wallet',       'กระเป๋าสตางค์ กระเป๋าเงิน'),
('Bags',         'กระเป๋าเป้ กระเป๋าสะพาย'),
('Clothing',     'เสื้อผ้า หมวก รองเท้า'),
('Books',        'หนังสือ ตำรา สมุดโน้ต'),
('Accessories',  'นาฬิกา แว่นตา เครื่องประดับ');

-- ---------------------------------------------------------------------
-- TAGS (8 แท็ก)
-- ---------------------------------------------------------------------
INSERT INTO tags (tag_name) VALUES
('valuable'), ('urgent'), ('waterproof'), ('black'),
('white'), ('small'), ('large'), ('engraved');

-- ---------------------------------------------------------------------
<<<<<<< HEAD
-- REPORTS (9 รายการ) -- ไม่มี REJECTED แล้ว, ไม่มี latitude/longitude
=======
-- REPORTS (10 รายการ) -- รวม REJECTED 1 รายการ (ตัวประกาศถูกปฏิเสธ ไม่ใช่ claim)
>>>>>>> ef18fe086e9a24c4ce017fa910f125807fcf614d
-- ---------------------------------------------------------------------
INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'LOST', 'ลืมโทรศัพท์ iPhone 14 ที่โรงอาหาร',
       'วางไว้บนโต๊ะแล้วลืม ตัวเครื่องสีดำ มีเคสใส',
       'โรงอาหารอาคาร A', now() - INTERVAL '2 days', 'OPEN'
FROM users u, categories c WHERE u.email='alice@example.com' AND c.category_name='Electronics';

INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'FOUND', 'พบกระเป๋าสตางค์ที่ลานจอดรถ',
       'กระเป๋าหนังสีน้ำตาล มีบัตรนักศึกษาและเงินสด',
       'ลานจอดรถ B', now() - INTERVAL '1 day', 'MATCH_PENDING'
FROM users u, categories c WHERE u.email='bob@example.com' AND c.category_name='Wallet';

INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'LOST', 'กุญแจรถหายที่ห้องสมุด',
       'พวงกุญแจมีที่ห้อยรูปหมี',
       'ห้องสมุดชั้น 3', now() - INTERVAL '5 hours', 'OPEN'
FROM users u, categories c WHERE u.email='carol@example.com' AND c.category_name='Keys';

INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'FOUND', 'พบหูฟัง AirPods ที่ห้องเรียน',
       'พบใต้เก้าอี้แถวหลัง',
       'อาคารเรียน 2 ห้อง 201', now() - INTERVAL '3 days', 'CLAIMED'
FROM users u, categories c WHERE u.email='dave@example.com' AND c.category_name='Electronics';

INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'LOST', 'บัตรนักศึกษาหาย',
       'บัตรนักศึกษาสีฟ้า',
       'หน้าตึกคณะวิศวกรรม', now() - INTERVAL '6 hours', 'OPEN'
FROM users u, categories c WHERE u.email='heidi@example.com' AND c.category_name='Documents';

INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'FOUND', 'พบนาฬิกาข้อมือที่สนามกีฬา',
       'นาฬิกาสายหนังสีดำ ยี่ห้อ Casio',
       'สนามกีฬากลางแจ้ง', now() - INTERVAL '4 days', 'CLOSED'
FROM users u, categories c WHERE u.email='alice@example.com' AND c.category_name='Accessories';

INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'LOST', 'กระเป๋าเป้สีดำหาย',
       'มีโน้ตบุ๊กและสายชาร์จอยู่ข้างใน',
       'รถเมล์สาย 24', now() - INTERVAL '12 hours', 'OPEN'
FROM users u, categories c WHERE u.email='bob@example.com' AND c.category_name='Bags';

INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'FOUND', 'พบหนังสือเรียนที่โรงอาหาร',
       'หนังสือ Calculus เล่มสีน้ำเงิน',
       'โรงอาหารอาคาร B', now() - INTERVAL '8 hours', 'OPEN'
FROM users u, categories c WHERE u.email='carol@example.com' AND c.category_name='Books';

INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'FOUND', 'พบแว่นตาที่ห้องน้ำ',
       'แว่นตากรอบทองวางอยู่บนอ่างล้างมือ',
       'ห้องน้ำอาคาร A ชั้น 2', now() - INTERVAL '30 minutes', 'OPEN'
FROM users u, categories c WHERE u.email='heidi@example.com' AND c.category_name='Accessories';

<<<<<<< HEAD
=======
INSERT INTO reports (user_id, category_id, type, title, description, location_name, event_timestamp, status)
SELECT u.user_id, c.category_id, 'LOST', 'เสื้อแจ็คเก็ตสีเทาหาย',
       'แจ็คเก็ตยี่ห้อ Uniqlo ไซส์ M',
       'ห้องเรียนรวม 1', now() - INTERVAL '1 day', 'REJECTED'
FROM users u, categories c WHERE u.email='dave@example.com' AND c.category_name='Clothing';

>>>>>>> ef18fe086e9a24c4ce017fa910f125807fcf614d
-- ---------------------------------------------------------------------
-- REPORT IMAGES (8 รายการ)
-- ---------------------------------------------------------------------
INSERT INTO report_images (report_id, image_url)
SELECT report_id, 'https://i.pinimg.com/originals/9c/45/b2/9c45b2d9911a63db51daa797b0a4d480.jpg' FROM reports WHERE title LIKE 'ลืมโทรศัพท์%'
UNION ALL
SELECT report_id, 'https://jacob.co.th/cdn/shop/products/22452Brown-1.png?v=1670408313&width=1920' FROM reports WHERE title LIKE 'พบกระเป๋าสตางค์%'
UNION ALL
SELECT report_id, 'https://xcdn.next.co.uk/common/items/default/default/itemimages/3_4Ratio/product/lge/G34808s.jpg' FROM reports WHERE title LIKE 'กุญแจรถหาย%'
UNION ALL
SELECT report_id, 'https://dl.lnwfile.com/hh8hx4.webp' FROM reports WHERE title LIKE 'พบหูฟัง%'
UNION ALL
SELECT report_id, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMw-nSzV9BpiEVg2kqG5q0lyPTvsx0eS19cOrtO94ISA&s=10' FROM reports WHERE title LIKE 'บัตรนักศึกษาหาย%'
UNION ALL
SELECT report_id, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRIlyDZxEh3DBqiV9F8dWq6VznSkalPKVtLg9EWi_Hl19UhyE-27VVZPZpy&s=10' FROM reports WHERE title LIKE 'พบนาฬิกา%'
UNION ALL
SELECT report_id, 'https://st.bigc-cs.com/cdn-cgi/image/format=webp,quality=90/public/media/catalog/product/59/38/3895780000459/3895780000459_1-20231121173202-.jpg' FROM reports WHERE title LIKE 'กระเป๋าเป้สีดำหาย%'
UNION ALL
SELECT report_id, 'https://jewel-cafe.co.th/wp-content/themes/new_jewel5745747/assets/images/kaitori/gold/gold_kind_glasses.jpg' FROM reports WHERE title LIKE 'พบแว่นตา%';

-- ---------------------------------------------------------------------
-- CLAIMS (6 รายการ) -- ระบบเคลมเต็มรูปแบบ: evidence, meeting_location/time
-- ---------------------------------------------------------------------
INSERT INTO claims (report_id, claimant_id, evidence_text, claim_status, meeting_location, meeting_time, resolved_at)
SELECT r.report_id, u.user_id, 'มีรูปถ่ายตอนซื้อและ serial number', 'APPROVED',
       'จุดนัดพบ ชั้น 1 อาคาร A', now() + INTERVAL '1 day', now() - INTERVAL '1 hour'
FROM reports r, users u WHERE r.title LIKE 'พบหูฟัง%' AND u.email='alice@example.com';

INSERT INTO claims (report_id, claimant_id, evidence_text, claim_status)
SELECT r.report_id, u.user_id, 'บัตรนักศึกษาตรงกับในกระเป๋า', 'PENDING'
FROM reports r, users u WHERE r.title LIKE 'พบกระเป๋าสตางค์%' AND u.email='heidi@example.com';

INSERT INTO claims (report_id, claimant_id, evidence_text, claim_status)
SELECT r.report_id, u.user_id, 'มีหลักฐานการซื้อ', 'PENDING'
FROM reports r, users u WHERE r.title LIKE 'พบนาฬิกา%' AND u.email='bob@example.com';

INSERT INTO claims (report_id, claimant_id, evidence_text, claim_status, resolved_at)
SELECT r.report_id, u.user_id, 'อธิบายรายละเอียดไม่ครบ', 'REJECTED', now() - INTERVAL '2 hours'
FROM reports r, users u WHERE r.title LIKE 'พบหนังสือเรียน%' AND u.email='dave@example.com';

INSERT INTO claims (report_id, claimant_id, evidence_text, claim_status)
SELECT r.report_id, u.user_id, 'บอกสีและยี่ห้อได้', 'PENDING'
FROM reports r, users u WHERE r.title LIKE 'พบแว่นตา%' AND u.email='carol@example.com';

INSERT INTO claims (report_id, claimant_id, evidence_text, claim_status)
SELECT r.report_id, u.user_id, 'มีประวัติการซื้อในอีเมล', 'PENDING'
FROM reports r, users u WHERE r.title LIKE 'กระเป๋าเป้สีดำหาย%' AND u.email='dave@example.com';

-- ---------------------------------------------------------------------
<<<<<<< HEAD
-- REPORT STATUS LOGS (7 รายการ) -- changed_by แทน actor_id
=======
-- REPORT STATUS LOGS (9 รายการ) -- changed_by แทน actor_id
>>>>>>> ef18fe086e9a24c4ce017fa910f125807fcf614d
-- ---------------------------------------------------------------------
INSERT INTO report_status_logs (report_id, changed_by, old_status, new_status)
SELECT r.report_id, u.user_id, NULL, 'OPEN'
FROM reports r, users u WHERE r.title LIKE 'ลืมโทรศัพท์%' AND u.email='alice@example.com'
UNION ALL
SELECT r.report_id, u.user_id, NULL, 'OPEN'
FROM reports r, users u WHERE r.title LIKE 'พบกระเป๋าสตางค์%' AND u.email='bob@example.com'
UNION ALL
SELECT r.report_id, u.user_id, 'OPEN', 'MATCH_PENDING'
FROM reports r, users u WHERE r.title LIKE 'พบกระเป๋าสตางค์%' AND u.email='eve@example.com'
UNION ALL
SELECT r.report_id, u.user_id, 'OPEN', 'CLAIMED'
FROM reports r, users u WHERE r.title LIKE 'พบหูฟัง%' AND u.email='grace@example.com'
UNION ALL
SELECT r.report_id, u.user_id, 'CLAIMED', 'CLOSED'
FROM reports r, users u WHERE r.title LIKE 'พบนาฬิกา%' AND u.email='grace@example.com'
UNION ALL
SELECT r.report_id, u.user_id, NULL, 'OPEN'
FROM reports r, users u WHERE r.title LIKE 'พบแว่นตา%' AND u.email='heidi@example.com'
UNION ALL
SELECT r.report_id, u.user_id, NULL, 'OPEN'
<<<<<<< HEAD
FROM reports r, users u WHERE r.title LIKE 'บัตรนักศึกษาหาย%' AND u.email='heidi@example.com';
=======
FROM reports r, users u WHERE r.title LIKE 'บัตรนักศึกษาหาย%' AND u.email='heidi@example.com'
UNION ALL
SELECT r.report_id, u.user_id, NULL, 'OPEN'
FROM reports r, users u WHERE r.title LIKE 'เสื้อแจ็คเก็ตสีเทาหาย%' AND u.email='dave@example.com'
UNION ALL
SELECT r.report_id, u.user_id, 'OPEN', 'REJECTED'
FROM reports r, users u WHERE r.title LIKE 'เสื้อแจ็คเก็ตสีเทาหาย%' AND u.email='grace@example.com';
>>>>>>> ef18fe086e9a24c4ce017fa910f125807fcf614d

-- ---------------------------------------------------------------------
-- REPORT TAGS (10 รายการ) -- Composite PK (report_id, tag_id)
-- ---------------------------------------------------------------------
INSERT INTO report_tags (report_id, tag_id)
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'ลืมโทรศัพท์%'      AND t.tag_name='valuable'
UNION ALL
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'ลืมโทรศัพท์%'      AND t.tag_name='black'
UNION ALL
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'พบกระเป๋าสตางค์%'   AND t.tag_name='valuable'
UNION ALL
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'พบกระเป๋าสตางค์%'   AND t.tag_name='small'
UNION ALL
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'กุญแจรถหาย%'       AND t.tag_name='urgent'
UNION ALL
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'พบหูฟัง%'          AND t.tag_name='small'
UNION ALL
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'บัตรนักศึกษาหาย%'   AND t.tag_name='urgent'
UNION ALL
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'พบนาฬิกา%'         AND t.tag_name='valuable'
UNION ALL
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'กระเป๋าเป้สีดำหาย%' AND t.tag_name='large'
UNION ALL
SELECT r.report_id, t.tag_id FROM reports r, tags t WHERE r.title LIKE 'พบแว่นตา%'         AND t.tag_name='engraved';

-- ---------------------------------------------------------------------
-- REPORT WATCHERS (10 รายการ) -- unique(report_id, user_id)
-- ---------------------------------------------------------------------
INSERT INTO report_watchers (report_id, user_id)
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'ลืมโทรศัพท์%'      AND u.email='bob@example.com'
UNION ALL
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'ลืมโทรศัพท์%'      AND u.email='carol@example.com'
UNION ALL
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'พบกระเป๋าสตางค์%'   AND u.email='alice@example.com'
UNION ALL
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'พบกระเป๋าสตางค์%'   AND u.email='heidi@example.com'
UNION ALL
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'กุญแจรถหาย%'       AND u.email='dave@example.com'
UNION ALL
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'พบหูฟัง%'          AND u.email='alice@example.com'
UNION ALL
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'บัตรนักศึกษาหาย%'   AND u.email='carol@example.com'
UNION ALL
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'พบนาฬิกา%'         AND u.email='bob@example.com'
UNION ALL
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'กระเป๋าเป้สีดำหาย%' AND u.email='dave@example.com'
UNION ALL
<<<<<<< HEAD
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'พบแว่นตา%'         AND u.email='carol@example.com';
=======
SELECT r.report_id, u.user_id FROM reports r, users u WHERE r.title LIKE 'พบแว่นตา%'         AND u.email='carol@example.com';
>>>>>>> ef18fe086e9a24c4ce017fa910f125807fcf614d
