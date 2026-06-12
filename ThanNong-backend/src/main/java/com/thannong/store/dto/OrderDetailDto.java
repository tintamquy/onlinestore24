package com.thannong.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO chi tiết đơn hàng (1 dòng sản phẩm trong order).
 */
@Getter
@Setter
public class OrderDetailDto {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private Integer quantityOrder;
    private BigDecimal priceEach;
    /** Thành tiền = priceEach × quantityOrder */
    private BigDecimal subTotal;
}
