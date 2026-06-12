-- ===========================================================
-- DỮ LIỆU ĐẦY ĐỦ CHO SHOP THẦN NÔNG (Upload lên phpMyAdmin online)
-- Chạy đoạn code này trong phpMyAdmin hoặc các công cụ SQL online (như Aiven MySQL)
-- ===========================================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Bảng t_role
CREATE TABLE IF NOT EXISTS `t_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_role_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `t_role` (`id`, `name`) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_CUSTOMER');

-- 2. Bảng t_user
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `t_user` (`id`, `username`, `password`, `created_at`, `updated_at`, `deleted`) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), NOW(), b'0');

-- 3. Bảng t_user_role
CREATE TABLE IF NOT EXISTS `t_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK_role_id` (`role_id`),
  CONSTRAINT `FK_user_id` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `t_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- 4. Bảng offices
CREATE TABLE IF NOT EXISTS `offices` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `city` varchar(50) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `address_line` varchar(50) NOT NULL,
  `state` varchar(50) DEFAULT NULL,
  `country` varchar(50) NOT NULL,
  `territory` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `offices` (`id`, `city`, `phone`, `address_line`, `state`, `country`, `territory`) VALUES
(1, 'Hà Nội', '024-3826-0000', '123 Phố Huế, Hai Bà Trưng', 'Hà Nội', 'Việt Nam', 'Miền Bắc'),
(2, 'TP.HCM', '028-3823-0000', '456 Nguyễn Huệ, Quận 1', 'TP.HCM', 'Việt Nam', 'Miền Nam');

-- 5. Bảng employees
CREATE TABLE IF NOT EXISTS `employees` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `last_name` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `extension` varchar(10) NOT NULL,
  `email` varchar(100) NOT NULL,
  `office_code` int(11) NOT NULL,
  `report_to` int(11) DEFAULT NULL,
  `job_title` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_employee_office` (`office_code`),
  KEY `FK_employee_manager` (`report_to`),
  CONSTRAINT `FK_employee_manager` FOREIGN KEY (`report_to`) REFERENCES `employees` (`id`),
  CONSTRAINT `FK_employee_office` FOREIGN KEY (`office_code`) REFERENCES `offices` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `employees` (`id`, `last_name`, `first_name`, `extension`, `email`, `office_code`, `report_to`, `job_title`) VALUES
(1, 'Nguyễn', 'Văn Quản Lý', 'x001', 'manager@thannong.vn', 1, NULL, 'Giám đốc'),
(2, 'Trần', 'Thị Sales', 'x002', 'sales@thannong.vn', 1, 1, 'Nhân viên kinh doanh');

-- 6. Bảng product_lines
CREATE TABLE IF NOT EXISTS `product_lines` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_line` varchar(50) NOT NULL,
  `description` varchar(2500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_product_line` (`product_line`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `product_lines` (`id`, `product_line`, `description`) VALUES
(1, 'Chăm sóc tóc', 'Các sản phẩm dầu gội, dầu xả từ thảo dược thiên nhiên'),
(2, 'Dưỡng da', 'Kem dưỡng trắng, kem trang da từ chiết xuất thảo mộc'),
(3, 'Sữa rửa mặt', 'Sữa rửa mặt Javin và các sản phẩm làm sạch da mặt'),
(4, 'Vệ sinh cá nhân', 'Sản phẩm vệ sinh thảo dược tự nhiên, an toàn cho mọi làn da');

-- 7. Bảng products
CREATE TABLE IF NOT EXISTS `products` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_code` varchar(50) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `product_description` varchar(2500) NOT NULL,
  `product_line_id` int(11) NOT NULL,
  `product_scale` varchar(50) NOT NULL,
  `product_vendor` varchar(50) NOT NULL,
  `quantity_in_stock` int(11) NOT NULL,
  `buy_price` decimal(10,2) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_product_code` (`product_code`),
  KEY `FK_product_line` (`product_line_id`),
  CONSTRAINT `FK_product_line` FOREIGN KEY (`product_line_id`) REFERENCES `product_lines` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `products` (`id`, `product_code`, `product_name`, `product_description`, `product_line_id`, `product_scale`, `product_vendor`, `quantity_in_stock`, `buy_price`, `image_url`, `deleted`) VALUES
(1, 'TN-DG-001', 'Dầu Gội Thảo Dược Thiên Nhiên Vui', 'Dầu gội từ các loại thảo dược quý hiếm, phục hồi tóc hư tổn, giảm gàu hiệu quả. Thành phần từ bồ kết, sả chanh, bưởi thiên nhiên 100%.', 1, '200ml', 'Thần Nông Herbs', 100, 85000.00, 'dau-goi-thao-duoc-thien-nhien-vui.jpg', b'0'),
(2, 'TN-DG-002', 'Dầu Gội Thảo Dược Thiên Nhiên Vui (Chai lớn)', 'Phiên bản chai lớn 400ml tiết kiệm hơn, cùng công thức thảo dược quý từ bồ kết và sả.', 1, '400ml', 'Thần Nông Herbs', 80, 155000.00, 'dau-goi-thao-duoc-thien-nhien-vui-1.jpg', b'0'),
(3, 'TN-DX-001', 'Dầu Xả Thảo Dược Dưỡng Mềm Tóc', 'Dầu xả phục hồi tóc chuyên sâu với chiết xuất từ nha đam, hoa nhài và thảo mộc tự nhiên. Giúp tóc mềm mượt, bóng khỏe.', 1, '200ml', 'Thần Nông Herbs', 90, 90000.00, 'dau-xa-thao-duoc.jpg', b'0'),
(4, 'TN-KD-001', 'Kem Dưỡng Trắng Da Thảo Dược', 'Kem dưỡng trắng với tinh chất nghệ và collagen tự nhiên, giúp làn da sáng mịn, đều màu sau 4 tuần sử dụng.', 2, '50g', 'Thần Nông Beauty', 60, 125000.00, 'kem-duong-trang.jpg', b'0'),
(5, 'TN-KT-001', 'Kem Trang Da Thảo Dược Thiên Nhiên', 'Kem trang da từ nguyên liệu thiên nhiên thuần Việt. Không chứa mercury, paraben, an toàn cho da nhạy cảm.', 2, '30g', 'Thần Nông Beauty', 75, 95000.00, 'kem-trang-da.jpg', b'0'),
(6, 'TN-SR-001', 'Sữa Rửa Mặt Javin Thảo Dược', 'Sữa rửa mặt Javin với chiết xuất từ trà xanh và gừng tươi, làm sạch sâu lỗ chân lông, kiểm soát dầu nhờn.', 3, '150ml', 'Javin Cosmetics', 120, 75000.00, 'sua-rua-mat-javin.jpg', b'0'),
(7, 'TN-VS-001', 'Dung Dịch Vệ Sinh Thảo Dược Phụ Nữ', 'Dung dịch vệ sinh vùng kín từ thảo dược tự nhiên: diệp hạ châu, kinh giới, mùi tàu. pH cân bằng, an toàn dùng hàng ngày.', 4, '200ml', 'Thần Nông Herbs', 85, 110000.00, 've-sinh-thao-duoc.jpg', b'0');

-- 8. Bảng customers
CREATE TABLE IF NOT EXISTS `customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `last_name` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `phone_number` varchar(50) NOT NULL,
  `address` varchar(255) NOT NULL,
  `city` varchar(50) NOT NULL,
  `state` varchar(50) DEFAULT NULL,
  `postal_code` varchar(50) DEFAULT NULL,
  `country` varchar(50) NOT NULL,
  `sales_rep_employee_number` int(11) DEFAULT NULL,
  `credit_limit` decimal(10,2) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_customer_employee` (`sales_rep_employee_number`),
  KEY `FK_customer_user` (`user_id`),
  CONSTRAINT `FK_customer_employee` FOREIGN KEY (`sales_rep_employee_number`) REFERENCES `employees` (`id`),
  CONSTRAINT `FK_customer_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `customers` (`id`, `last_name`, `first_name`, `phone_number`, `address`, `city`, `state`, `country`, `sales_rep_employee_number`, `credit_limit`) VALUES
(1, 'Nguyễn', 'Thị Lan', '0901234567', '12 Lê Lợi, Phường 1', 'TP.HCM', 'TP.HCM', 'Việt Nam', 2, 10000000.00),
(2, 'Trần', 'Văn Minh', '0912345678', '45 Trần Phú, Hoàn Kiếm', 'Hà Nội', 'Hà Nội', 'Việt Nam', 2, 5000000.00);

-- 9. Bảng orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_date` date NOT NULL,
  `required_date` date NOT NULL,
  `shipped_date` date DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `comments` varchar(2500) DEFAULT NULL,
  `customer_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_order_customer` (`customer_id`),
  CONSTRAINT `FK_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `orders` (`id`, `order_date`, `required_date`, `status`, `comments`, `customer_id`) VALUES
(1, CURRENT_DATE(), DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), NULL, 'DONE', 'Giao hàng nhanh giúp mình nhé', 1);

-- 10. Bảng order_details
CREATE TABLE IF NOT EXISTS `order_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity_order` int(11) NOT NULL,
  `price_each` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_orderdetail_order` (`order_id`),
  KEY `FK_orderdetail_product` (`product_id`),
  CONSTRAINT `FK_orderdetail_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FK_orderdetail_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO `order_details` (`id`, `order_id`, `product_id`, `quantity_order`, `price_each`) VALUES
(1, 1, 1, 2, 85000.00),
(2, 1, 4, 1, 125000.00);

SET FOREIGN_KEY_CHECKS = 1;
