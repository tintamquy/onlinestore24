package com.thannong.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * DTO đánh giá sản phẩm.
 * Khách hàng gửi rating + comment cho sản phẩm đã mua.
 */
@Getter
@Setter
public class ReviewDto {
    private Long id;
    private Integer customerId;
    private String customerName;    // Họ + tên để hiển thị
    private Integer productId;
    private String productName;
    private Integer rating;         // 1 đến 5 sao
    private String comment;
    private Date createdAt;
}
