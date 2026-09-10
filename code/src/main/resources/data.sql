-- =====================================================================
-- MOCK DATA (5–10 รายการต่อตาราง)
-- รันหลัง schema.sql
-- =====================================================================

-- ---------------------------------------------------------------------
-- USERS (8 คน)
-- ---------------------------------------------------------------------
INSERT INTO users (firebase_uid, email, role) VALUES
('fb_uid_001', 'alice@example.com',   'USER'),
('fb_uid_002', 'bob@example.com',     'USER'),
('fb_uid_003', 'carol@example.com',   'USER'),
('fb_uid_004', 'dave@example.com',    'USER'),
('fb_uid_005', 'eve@example.com',     'STAFF'),
('fb_uid_006', 'frank@example.com',   'STAFF'),
('fb_uid_007', 'grace@example.com',   'ADMIN'),
('fb_uid_008', 'heidi@example.com',   'USER');

-- ---------------------------------------------------------------------
-- USER PROFILES (8 รายการ)
-- ---------------------------------------------------------------------
INSERT INTO user_profiles (user_id, full_name, phone_number, student_or_staff_id, avatar_url)
SELECT id, 'Alice Johnson',   '081-111-1111', 'STD65001', 'https://i.pravatar.cc/150?u=alice'   FROM users WHERE email='alice@example.com'
UNION ALL
SELECT id, 'Bob Smith',       '081-222-2222', 'STD65002', 'https://i.pravatar.cc/150?u=bob'     FROM users WHERE email='bob@example.com'
UNION ALL
SELECT id, 'Carol White',     '081-333-3333', 'STD65003', 'https://i.pravatar.cc/150?u=carol'   FROM users WHERE email='carol@example.com'
UNION ALL
SELECT id, 'Dave Brown',      '081-444-4444', 'STD65004', 'https://i.pravatar.cc/150?u=dave'    FROM users WHERE email='dave@example.com'
UNION ALL
SELECT id, 'Eve Davis',       '081-555-5555', 'STF10001', 'https://i.pravatar.cc/150?u=eve'     FROM users WHERE email='eve@example.com'
UNION ALL
SELECT id, 'Frank Miller',    '081-666-6666', 'STF10002', 'https://i.pravatar.cc/150?u=frank'   FROM users WHERE email='frank@example.com'
UNION ALL
SELECT id, 'Grace Wilson',    '081-777-7777', 'ADM10001', 'https://i.pravatar.cc/150?u=grace'   FROM users WHERE email='grace@example.com'
UNION ALL
SELECT id, 'Heidi Moore',     '081-888-8888', 'STD65008', 'https://i.pravatar.cc/150?u=heidi'   FROM users WHERE email='heidi@example.com';

-- ---------------------------------------------------------------------
-- CATEGORIES (8 หมวด)
-- ---------------------------------------------------------------------
INSERT INTO categories (name, description) VALUES
('Electronics',   'มือถือ แท็บเล็ต หูฟัง โน้ตบุ๊ก'),
('Documents',     'บัตรประชาชน ใบขับขี่ พาสปอร์ต'),
('Keys',          'กุญแจบ้าน กุญแจรถ'),
('Wallet',        'กระเป๋าสตางค์ กระเป๋าเงิน'),
('Bags',          'กระเป๋าเป้ กระเป๋าสะพาย'),
('Clothing',      'เสื้อผ้า หมวก รองเท้า'),
('Books',         'หนังสือ ตำรา สมุดโน้ต'),
('Accessories',   'นาฬิกา แว่นตา เครื่องประดับ');

-- ---------------------------------------------------------------------
-- TAGS (8 แท็ก)
-- ---------------------------------------------------------------------
INSERT INTO tags (name) VALUES
('valuable'), ('urgent'), ('waterproof'), ('black'),
('white'), ('small'), ('large'), ('engraved');

-- ---------------------------------------------------------------------
-- REPORTS (10 รายการ)
-- ---------------------------------------------------------------------
INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'LOST', 'ลืมโทรศัพท์ iPhone 14 ที่โรงอาหาร',
       'วางไว้บนโต๊ะแล้วลืม ตัวเครื่องสีดำ มีเคสใส',
       'โรงอาหารอาคาร A', 13.75630000, 100.50180000,
       now() - INTERVAL '2 days', 'OPEN'
FROM users u, categories c WHERE u.email='alice@example.com' AND c.name='Electronics';

INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'FOUND', 'พบกระเป๋าสตางค์ที่ลานจอดรถ',
       'กระเป๋าหนังสีน้ำตาล มีบัตรนักศึกษาและเงินสด',
       'ลานจอดรถ B', 13.75600000, 100.50200000,
       now() - INTERVAL '1 day', 'MATCH_PENDING'
FROM users u, categories c WHERE u.email='bob@example.com' AND c.name='Wallet';

INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'LOST', 'กุญแจรถหายที่ห้องสมุด',
       'พวงกุญแจมีที่ห้อยรูปหมี',
       'ห้องสมุดชั้น 3', 13.75590000, 100.50150000,
       now() - INTERVAL '5 hours', 'OPEN'
FROM users u, categories c WHERE u.email='carol@example.com' AND c.name='Keys';

INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'FOUND', 'พบหูฟัง AirPods ที่ห้องเรียน',
       'พบใต้เก้าอี้แถวหลัง',
       'อาคารเรียน 2 ห้อง 201', 13.75620000, 100.50170000,
       now() - INTERVAL '3 days', 'CLAIMED'
FROM users u, categories c WHERE u.email='dave@example.com' AND c.name='Electronics';

INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'LOST', 'บัตรนักศึกษาหาย',
       'บัตรนักศึกษาเลข STD65008 สีฟ้า',
       'หน้าตึกคณะวิศวกรรม', 13.75650000, 100.50230000,
       now() - INTERVAL '6 hours', 'OPEN'
FROM users u, categories c WHERE u.email='heidi@example.com' AND c.name='Documents';

INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'FOUND', 'พบนาฬิกาข้อมือที่สนามกีฬา',
       'นาฬิกาสายหนังสีดำ ยี่ห้อ Casio',
       'สนามกีฬากลางแจ้ง', 13.75580000, 100.50250000,
       now() - INTERVAL '4 days', 'CLOSED'
FROM users u, categories c WHERE u.email='alice@example.com' AND c.name='Accessories';

INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'LOST', 'กระเป๋าเป้สีดำหาย',
       'มีโน้ตบุ๊กและสายชาร์จอยู่ข้างใน',
       'รถเมล์สาย 24', 13.75670000, 100.50270000,
       now() - INTERVAL '12 hours', 'OPEN'
FROM users u, categories c WHERE u.email='bob@example.com' AND c.name='Bags';

INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'FOUND', 'พบหนังสือเรียนที่โรงอาหาร',
       'หนังสือ Calculus เล่มสีน้ำเงิน',
       'โรงอาหารอาคาร B', 13.75610000, 100.50190000,
       now() - INTERVAL '8 hours', 'OPEN'
FROM users u, categories c WHERE u.email='carol@example.com' AND c.name='Books';

INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'LOST', 'เสื้อแจ็คเก็ตสีเทาหาย',
       'แจ็คเก็ตยี่ห้อ Uniqlo ไซส์ M',
       'ห้องเรียนรวม 1', 13.75570000, 100.50140000,
       now() - INTERVAL '1 day', 'REJECTED'
FROM users u, categories c WHERE u.email='dave@example.com' AND c.name='Clothing';

INSERT INTO reports (reporter_id, category_id, type, title, description, location_name, latitude, longitude, event_timestamp, status)
SELECT u.id, c.id, 'FOUND', 'พบแว่นตาที่ห้องน้ำ',
       'แว่นตากรอบทองวางอยู่บนอ่างล้างมือ',
       'ห้องน้ำอาคาร A ชั้น 2', 13.75640000, 100.50160000,
       now() - INTERVAL '30 minutes', 'OPEN'
FROM users u, categories c WHERE u.email='heidi@example.com' AND c.name='Accessories';

-- ---------------------------------------------------------------------
-- REPORT IMAGES (8 รายการ)
-- ---------------------------------------------------------------------
INSERT INTO report_images (report_id, image_url)
SELECT id, 'https://i.pinimg.com/originals/9c/45/b2/9c45b2d9911a63db51daa797b0a4d480.jpg' FROM reports WHERE title LIKE 'ลืมโทรศัพท์%'
UNION ALL
SELECT id, 'https://jacob.co.th/cdn/shop/products/22452Brown-1.png?v=1670408313&width=1920' FROM reports WHERE title LIKE 'พบกระเป๋าสตางค์%'
UNION ALL
SELECT id, 'https://xcdn.next.co.uk/common/items/default/default/itemimages/3_4Ratio/product/lge/G34808s.jpg?im=Resize,width=750' FROM reports WHERE title LIKE 'กุญแจรถหาย%'
UNION ALL
SELECT id, 'https://dl.lnwfile.com/hh8hx4.webp' FROM reports WHERE title LIKE 'พบหูฟัง%'
UNION ALL
SELECT id, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMw-nSzV9BpiEVg2kqG5q0lyPTvsx0eS19cOrtO94ISA&s=10' FROM reports WHERE title LIKE 'บัตรนักศึกษาหาย%'
UNION ALL
SELECT id, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRIlyDZxEh3DBqiV9F8dWq6VznSkalPKVtLg9EWi_Hl19UhyE-27VVZPZpy&s=10' FROM reports WHERE title LIKE 'พบนาฬิกา%'
UNION ALL
SELECT id, 'https://st.bigc-cs.com/cdn-cgi/image/format=webp,quality=90/public/media/catalog/product/59/38/3895780000459/3895780000459_1-20231121173202-.jpg' FROM reports WHERE title LIKE 'กระเป๋าเป้สีดำหาย%'
UNION ALL
SELECT id, 'https://jewel-cafe.co.th/wp-content/themes/new_jewel5745747/assets/images/kaitori/gold/gold_kind_glasses.jpg' FROM reports WHERE title LIKE 'พบแว่นตา%';

-- ---------------------------------------------------------------------
-- CLAIMS (6 รายการ)
-- ---------------------------------------------------------------------
INSERT INTO claims (report_id, claimer_id, proof_description, status)
SELECT r.id, u.id, 'มีรูปถ่ายตอนซื้อและ serial number', 'APPROVED'
FROM reports r, users u WHERE r.title LIKE 'พบหูฟัง%' AND u.email='alice@example.com';

INSERT INTO claims (report_id, claimer_id, proof_description, status)
SELECT r.id, u.id, 'บัตรนักศึกษาตรงกับในกระเป๋า', 'PENDING'
FROM reports r, users u WHERE r.title LIKE 'พบกระเป๋าสตางค์%' AND u.email='heidi@example.com';

INSERT INTO claims (report_id, claimer_id, proof_description, status)
SELECT r.id, u.id, 'มีหลักฐานการซื้อ', 'PENDING'
FROM reports r, users u WHERE r.title LIKE 'พบนายิกา%' AND u.email='bob@example.com';

INSERT INTO claims (report_id, claimer_id, proof_description, status)
SELECT r.id, u.id, 'อธิบายรายละเอียดได้', 'REJECTED'
FROM reports r, users u WHERE r.title LIKE 'พบหนังสือเรียน%' AND u.email='dave@example.com';

INSERT INTO claims (report_id, claimer_id, proof_description, status)
SELECT r.id, u.id, 'บอกสีและยี่ห้อได้', 'PENDING'
FROM reports r, users u WHERE r.title LIKE 'พบแว่นตา%' AND u.email='carol@example.com';

INSERT INTO claims (report_id, claimer_id, proof_description, status)
SELECT r.id, u.id, 'มีประวัติการซื้อในอีเมล', 'PENDING'
FROM reports r, users u WHERE r.title LIKE 'กระเป๋าเป้สีดำหาย%' AND u.email='dave@example.com';

-- ---------------------------------------------------------------------
-- REPORT STATUS LOGS (8 รายการ)
-- ---------------------------------------------------------------------
INSERT INTO report_status_logs (report_id, actor_id, old_status, new_status, remark)
SELECT r.id, u.id, NULL, 'OPEN', 'สร้างรายงานใหม่'
FROM reports r, users u WHERE r.title LIKE 'ลืมโทรศัพท์%' AND u.email='alice@example.com'
UNION ALL
SELECT r.id, u.id, NULL, 'OPEN', 'สร้างรายงานใหม่'
FROM reports r, users u WHERE r.title LIKE 'พบกระเป๋าสตางค์%' AND u.email='bob@example.com'
UNION ALL
SELECT r.id, u.id, 'OPEN', 'MATCH_PENDING', 'มีผู้แจ้งขอรับสิทธิ์'
FROM reports r, users u WHERE r.title LIKE 'พบกระเป๋าสตางค์%' AND u.email='eve@example.com'
UNION ALL
SELECT r.id, u.id, 'OPEN', 'CLAIMED', 'อนุมัติคำขอ'
FROM reports r, users u WHERE r.title LIKE 'พบหูฟัง%' AND u.email='frank@example.com'
UNION ALL
SELECT r.id, u.id, 'CLAIMED', 'CLOSED', 'ปิดเคสเรียบร้อย'
FROM reports r, users u WHERE r.title LIKE 'พบนายิกา%' AND u.email='grace@example.com'
UNION ALL
SELECT r.id, u.id, 'OPEN', 'REJECTED', 'ข้อมูลไม่เพียงพอ'
FROM reports r, users u WHERE r.title LIKE 'เสื้อแจ็คเก็ตสีเทาหาย%' AND u.email='grace@example.com'
UNION ALL
SELECT r.id, u.id, NULL, 'OPEN', 'สร้างรายงานใหม่'
FROM reports r, users u WHERE r.title LIKE 'พบแว่นตา%' AND u.email='heidi@example.com'
UNION ALL
SELECT r.id, u.id, NULL, 'OPEN', 'สร้างรายงานใหม่'
FROM reports r, users u WHERE r.title LIKE 'บัตรนักศึกษาหาย%' AND u.email='heidi@example.com';

-- ---------------------------------------------------------------------
-- REPORT TAGS (10 รายการ)
-- ---------------------------------------------------------------------
INSERT INTO report_tags (report_id, tag_id)
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'ลืมโทรศัพท์%'    AND t.name='valuable'
UNION ALL
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'ลืมโทรศัพท์%'    AND t.name='black'
UNION ALL
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'พบกระเป๋าสตางค์%' AND t.name='valuable'
UNION ALL
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'พบกระเป๋าสตางค์%' AND t.name='small'
UNION ALL
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'กุญแจรถหาย%'    AND t.name='urgent'
UNION ALL
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'พบหูฟัง%'      AND t.name='small'
UNION ALL
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'บัตรนักศึกษาหาย%' AND t.name='urgent'
UNION ALL
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'พบนายิกา%'     AND t.name='valuable'
UNION ALL
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'กระเป๋าเป้สีดำหาย%' AND t.name='large'
UNION ALL
SELECT r.id, t.id FROM reports r, tags t WHERE r.title LIKE 'พบแว่นตา%'     AND t.name='engraved';

-- ---------------------------------------------------------------------
-- REPORT WATCHERS (10 รายการ)
-- ---------------------------------------------------------------------
INSERT INTO report_watchers (user_id, report_id)
SELECT u.id, r.id FROM users u, reports r WHERE u.email='bob@example.com'   AND r.title LIKE 'ลืมโทรศัพท์%'
UNION ALL
SELECT u.id, r.id FROM users u, reports r WHERE u.email='carol@example.com' AND r.title LIKE 'ลืมโทรศัพท์%'
UNION ALL
SELECT u.id, r.id FROM users u, reports r WHERE u.email='alice@example.com' AND r.title LIKE 'พบกระเป๋าสตางค์%'
UNION ALL
SELECT u.id, r.id FROM users u, reports r WHERE u.email='heidi@example.com' AND r.title LIKE 'พบกระเป๋าสตางค์%'
UNION ALL
SELECT u.id, r.id FROM users u, reports r WHERE u.email='dave@example.com'  AND r.title LIKE 'กุญแจรถหาย%'
UNION ALL
SELECT u.id, r.id FROM users u, reports r WHERE u.email='alice@example.com' AND r.title LIKE 'พบหูฟัง%'
UNION ALL
SELECT u.id, r.id FROM users u, reports r WHERE u.email='carol@example.com' AND r.title LIKE 'บัตรนักศึกษาหาย%'
UNION ALL
SELECT u.id, r.id FROM users u, reports r WHERE u.email='bob@example.com'   AND r.title LIKE 'พบนายิกา%'
UNION ALL
SELECT u.id, r.id FROM users u, reports r WHERE u.email='dave@example.com'  AND r.title LIKE 'กระเป๋าเป้สีดำหาย%'
UNION ALL
SELECT u.id, r.id FROM users u, reports r WHERE u.email='carol@example.com' AND r.title LIKE 'พบแว่นตา%';