package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entity tương ứng bảng customers (khách hàng).
 * Khóa định danh chính trên storefront là phone_number (số điện thoại).
 * Khi checkout: nếu SĐT trùng → cập nhật, chưa có → tạo mới.
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Họ khách hàng */
    @Column(name = "last_name", length = 50)
    private String lastName;

    /** Tên khách hàng */
    @Column(name = "first_name", length = 50)
    private String firstName;

    /**
     * Số điện thoại — là trường định danh khách hàng trên storefront.
     * Dùng để kiểm tra khách đã tồn tại hay chưa khi đặt hàng.
     */
    @Column(name = "phone_number", length = 50, nullable = false, unique = true)
    private String phoneNumber;

    /** Địa chỉ giao hàng */
    @Column(length = 255)
    private String address;

    /** Thành phố */
    @Column(length = 50)
    private String city;

    /** Tỉnh/Bang */
    @Column(length = 50)
    private String state;

    /** Mã bưu điện */
    @Column(name = "postal_code", length = 50)
    private String postalCode;

    /** Quốc gia */
    @Column(length = 50)
    private String country;

    /**
     * Nhân viên kinh doanh phụ trách khách hàng này.
     * Quan hệ Many-to-One: nhiều KH có thể do 1 nhân viên phụ trách.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_rep_employee_number")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Employee salesRepEmployee;

    /** Hạn mức tín dụng của khách hàng (VNĐ) */
    @Column(name = "credit_limit")
    private Integer creditLimit;

}
