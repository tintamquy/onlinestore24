package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity tương ứng bảng order_details (chi tiết đơn hàng).
 * Mỗi order_detail là 1 dòng sản phẩm trong đơn hàng:
 * ví dụ "5 chai Dầu gội thảo dược @ 85.000đ/chai".
 */
@Entity
@Table(name = "order_details")
@Getter
@Setter
public class OrderDetail {

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Đơn hàng chứa dòng này.
     * Quan hệ Many-to-One: nhiều order_detail thuộc 1 order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Sản phẩm trong dòng này.
     * Quan hệ Many-to-One: nhiều order_detail có thể trỏ đến 1 sản phẩm.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Số lượng sản phẩm được đặt */
    @Column(name = "quantity_order", nullable = false)
    private Integer quantityOrder;

    /** Đơn giá tại thời điểm đặt hàng (snapshot giá) */
    @Column(name = "price_each", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceEach;

}
