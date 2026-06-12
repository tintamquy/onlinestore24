-- ===========================================================
-- Dữ liệu mẫu ban đầu cho Shop Thần Nông
-- File: data.sql
-- Chạy tự động khi Spring Boot khởi động (spring.sql.init.mode=always)
-- ===========================================================

-- ===== 1. Roles =====
INSERT IGNORE INTO t_role (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO t_role (id, name) VALUES (2, 'ROLE_CUSTOMER');

-- ===== 2. Tài khoản Admin =====
-- Mật khẩu "admin123" đã mã hóa BCrypt
INSERT IGNORE INTO t_user (id, username, password, deleted, created_at, updated_at)
VALUES (1, 'admin', '$2a$10$wZdXg4noG3Qd5CNnvZX9rOO2N0IvcM9s2gcwGNL8BiFkdnbv1VGGC', false, NOW(), NOW());

-- Gán role ADMIN cho tài khoản admin
INSERT IGNORE INTO t_user_role (user_id, role_id) VALUES (1, 1);

-- ===== 3. Văn phòng =====
INSERT IGNORE INTO offices (id, city, phone, address_line, state, country, territory)
VALUES (1, 'Hà Nội', '024-3826-0000', '123 Phố Huế, Hai Bà Trưng', 'Hà Nội', 'Việt Nam', 'Miền Bắc');

INSERT IGNORE INTO offices (id, city, phone, address_line, state, country, territory)
VALUES (2, 'TP.HCM', '028-3823-0000', '456 Nguyễn Huệ, Quận 1', 'TP.HCM', 'Việt Nam', 'Miền Nam');

-- ===== 4. Nhân viên =====
INSERT IGNORE INTO employees (id, last_name, first_name, extension, email, office_code, report_to, job_title)
VALUES (1, 'Nguyễn', 'Văn Quản Lý', 'x001', 'manager@thannong.vn', 1, NULL, 'Giám đốc');

INSERT IGNORE INTO employees (id, last_name, first_name, extension, email, office_code, report_to, job_title)
VALUES (2, 'Trần', 'Thị Sales', 'x002', 'sales@thannong.vn', 1, 1, 'Nhân viên kinh doanh');

-- ===== 5. Dòng sản phẩm (Danh mục) =====
INSERT IGNORE INTO product_lines (id, product_line, description)
VALUES (1, 'Chăm sóc tóc', 'Các sản phẩm dầu gội, dầu xả từ thảo dược thiên nhiên');

INSERT IGNORE INTO product_lines (id, product_line, description)
VALUES (2, 'Dưỡng da', 'Kem dưỡng trắng, kem trang da từ chiết xuất thảo mộc');

INSERT IGNORE INTO product_lines (id, product_line, description)
VALUES (3, 'Sữa rửa mặt', 'Sữa rửa mặt Javin và các sản phẩm làm sạch da mặt');

INSERT IGNORE INTO product_lines (id, product_line, description)
VALUES (4, 'Vệ sinh cá nhân', 'Sản phẩm vệ sinh thảo dược tự nhiên, an toàn cho mọi làn da');

-- ===== 6. Sản phẩm mẫu =====
INSERT IGNORE INTO products
    (id, product_code, product_name, product_description, product_line_id,
     product_scale, product_vendor, quantity_in_stock, buy_price, image_url, deleted)
VALUES
(1, 'TN-DG-001', 'Dầu Gội Thảo Dược Thiên Nhiên Vui',
 'Dầu gội từ các loại thảo dược quý hiếm, phục hồi tóc hư tổn, giảm gàu hiệu quả. Thành phần từ bồ kết, sả chanh, bưởi thiên nhiên 100%.',
 1, '200ml', 'Thần Nông Herbs', 100, 85000, 'dau-goi-thao-duoc-thien-nhien-vui.jpg', false),

(2, 'TN-DG-002', 'Dầu Gội Thảo Dược Thiên Nhiên Vui (Chai lớn)',
 'Phiên bản chai lớn 400ml tiết kiệm hơn, cùng công thức thảo dược quý từ bồ kết và sả.',
 1, '400ml', 'Thần Nông Herbs', 80, 155000, 'dau-goi-thao-duoc-thien-nhien-vui-1.jpg', false),

(3, 'TN-DX-001', 'Dầu Xả Thảo Dược Dưỡng Mềm Tóc',
 'Dầu xả phục hồi tóc chuyên sâu với chiết xuất từ nha đam, hoa nhài và thảo mộc tự nhiên. Giúp tóc mềm mượt, bóng khỏe.',
 1, '200ml', 'Thần Nông Herbs', 90, 90000, 'dau-xa-thao-duoc-1200x857-1.jpg', false),

(4, 'TN-KD-001', 'Kem Dưỡng Trắng Da Thảo Dược',
 'Kem dưỡng trắng với tinh chất nghệ và collagen tự nhiên, giúp làn da sáng mịn, đều màu sau 4 tuần sử dụng.',
 2, '50g', 'Thần Nông Beauty', 60, 125000, 'kem-duong-trang-body-namirea.jpg', false),

(5, 'TN-KT-001', 'Kem Trang Da Thảo Dược Thiên Nhiên',
 'Kem trang da từ nguyên liệu thiên nhiên thuần Việt. Không chứa mercury, paraben, an toàn cho da nhạy cảm.',
 2, '30g', 'Thần Nông Beauty', 75, 95000, 'kem-mo-nam-trang-da-dr-melasma-cream-1.jpg', false),

(6, 'TN-SR-001', 'Sữa Rửa Mặt Javin Thảo Dược',
 'Sữa rửa mặt Javin với chiết xuất từ trà xanh và gừng tươi, làm sạch sâu lỗ chân lông, kiểm soát dầu nhờn.',
 3, '150ml', 'Javin Cosmetics', 120, 75000, 'sua-rua-mat-tao-bot-javin.jpg', false),

(7, 'TN-VS-001', 'Dung Dịch Vệ Sinh Thảo Dược Phụ Nữ',
 'Dung dịch vệ sinh vùng kín từ thảo dược tự nhiên: diệp hạ châu, kinh giới, mùi tàu. pH cân bằng, an toàn dùng hàng ngày.',
 4, '200ml', 'Thần Nông Herbs', 85, 110000, 'DDVS-VUI.jpg', false);

-- ===== 7. Khách hàng mẫu =====
INSERT IGNORE INTO customers (id, last_name, first_name, phone_number, address, city, state, country, sales_rep_employee_number, credit_limit)
VALUES (1, 'Nguyễn', 'Thị Lan', '0901234567', '12 Lê Lợi, Phường 1', 'TP.HCM', 'TP.HCM', 'Việt Nam', 2, 10000000);

INSERT IGNORE INTO customers (id, last_name, first_name, phone_number, address, city, state, country, sales_rep_employee_number, credit_limit)
VALUES (2, 'Trần', 'Văn Minh', '0912345678', '45 Trần Phú, Hoàn Kiếm', 'Hà Nội', 'Hà Nội', 'Việt Nam', 2, 5000000);

-- ===== 8. Đơn hàng mẫu =====
INSERT IGNORE INTO orders (id, order_date, required_date, status, comments, customer_id)
VALUES (1, NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 'DONE', 'Giao hàng nhanh giúp mình nhé', 1);

-- Chi tiết đơn hàng mẫu
INSERT IGNORE INTO order_details (id, order_id, product_id, quantity_order, price_each)
VALUES (1, 1, 1, 2, 85000.00);

INSERT IGNORE INTO order_details (id, order_id, product_id, quantity_order, price_each)
VALUES (2, 1, 4, 1, 125000.00);
