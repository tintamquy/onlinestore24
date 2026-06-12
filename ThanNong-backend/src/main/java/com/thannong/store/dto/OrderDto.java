package com.thannong.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * DTO đơn hàng: dùng cho cả tạo mới (POST) và đọc (GET).
 * Khi tạo mới, client gửi thông tin khách hàng + danh sách sản phẩm.
 */
@Getter
@Setter
public class OrderDto {
    private Integer id;
    private Date orderDate;
    private Date requiredDate;
    private Date shippedDate;
    private String status;
    private String comments;

    // Thông tin khách hàng (nhập khi checkout)
    private Integer customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerPhone;    // SĐT — dùng để tra cứu khách hàng cũ
    private String customerAddress;
    private String customerCity;
    private String customerCountry;

    /** Danh sách sản phẩm trong đơn hàng */
    private List<OrderDetailDto> orderDetails;

    /** Tổng tiền đơn hàng (tính tổng các OrderDetail) */
    private BigDecimal totalAmount;
}
