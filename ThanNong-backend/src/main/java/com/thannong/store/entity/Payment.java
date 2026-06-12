package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity tương ứng bảng payments (thanh toán).
 * Ghi lại lịch sử thanh toán của khách hàng.
 * Lưu ý: field "ammount" giữ nguyên typo theo ERD gốc của đề bài.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Khách hàng thực hiện thanh toán.
     * Quan hệ Many-to-One: 1 KH có thể có nhiều thanh toán.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /** Mã số chứng từ / séc thanh toán */
    @Column(name = "check_number", length = 50)
    private String checkNumber;

    /** Ngày thanh toán */
    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    /**
     * Số tiền thanh toán.
     * Tên field "ammount" giữ nguyên theo ERD gốc (có typo "ammount" thay vì "amount").
     */
    @Column(name = "ammount", precision = 10, scale = 2)
    private BigDecimal ammount;

}
