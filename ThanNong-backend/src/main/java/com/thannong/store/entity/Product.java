package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity tương ứng bảng products (sản phẩm).
 * Quan hệ: nhiều sản phẩm thuộc một product_line (dòng SP).
 */
@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Mã sản phẩm nội bộ (ví dụ: TN-001) */
    @Column(name = "product_code", length = 50)
    private String productCode;

    /** Tên sản phẩm */
    @Column(name = "product_name", length = 255, nullable = false)
    private String productName;

    /** Mô tả chi tiết sản phẩm */
    @Column(name = "product_description", length = 2500)
    private String productDescription;

    /**
     * Dòng sản phẩm (danh mục).
     * Quan hệ Many-to-One: nhiều SP thuộc 1 dòng SP.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_line_id")
    private ProductLine productLine;

    /** Tỷ lệ kích cỡ (ví dụ: 100ml, 200ml) */
    @Column(name = "product_scale", length = 50)
    private String productScale;

    /** Nhà cung cấp / thương hiệu */
    @Column(name = "product_vendor", length = 50)
    private String productVendor;

    /** Số lượng tồn kho */
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock = 0;

    /** Giá bán sản phẩm */
    @Column(name = "buy_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal buyPrice;

    /**
     * Đường dẫn ảnh sản phẩm.
     * Lưu dưới dạng tên file (ví dụ: "dau-goi.jpg").
     * Frontend gọi: GET /api/images/products/{imageName}
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Soft delete: true = đã xóa (không hiện trên storefront) */
    @Column(columnDefinition = "boolean default false")
    private boolean deleted = false;

}
