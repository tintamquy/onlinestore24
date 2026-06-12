package com.thannong.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO sản phẩm: dùng cho cả request (thêm/sửa) và response (đọc).
 * Tránh trả entity trực tiếp ra ngoài API.
 */
@Getter
@Setter
public class ProductDto {
    private Integer id;
    private String productCode;
    private String productName;
    private String productDescription;
    private Integer productLineId;
    private String productLineName;
    private String productScale;
    private String productVendor;
    private Integer quantityInStock;
    private BigDecimal buyPrice;
    /** URL ảnh (tên file, ví dụ: "dau-goi.jpg") */
    private String imageUrl;
    /** Điểm đánh giá trung bình (tính từ t_review) */
    private Double averageRating;
    /** Tổng số review */
    private Long reviewCount;
}
