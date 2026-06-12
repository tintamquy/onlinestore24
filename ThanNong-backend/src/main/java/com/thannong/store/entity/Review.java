package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entity tương ứng bảng t_review (đánh giá sản phẩm).
 * Chức năng Task 5 & 6: khách hàng đánh giá (rating 1-5 sao) và viết review.
 * Admin có thể xem và xóa review (Task 7).
 */
@Entity
@Table(name = "t_review")
@Getter
@Setter
public class Review {

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Khách hàng viết đánh giá.
     * Quan hệ Many-to-One: 1 KH có thể đánh giá nhiều SP.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * Sản phẩm được đánh giá.
     * Quan hệ Many-to-One: 1 SP có thể có nhiều đánh giá.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Số sao đánh giá.
     * Giá trị hợp lệ: 1 đến 5.
     */
    @Column(nullable = false)
    private Integer rating;

    /** Nội dung nhận xét */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /** Soft delete: true = admin đã xóa review này */
    @Column(columnDefinition = "boolean default false")
    private boolean deleted = false;

    /** Thời điểm tạo review */
    @Column(name = "created_at")
    private java.util.Date createdAt;

}
