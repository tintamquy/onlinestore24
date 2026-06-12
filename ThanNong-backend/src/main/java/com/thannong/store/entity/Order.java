package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity tương ứng bảng orders (đơn hàng).
 * Một đơn hàng thuộc về 1 khách hàng và có thể chứa nhiều order_details.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Ngày đặt hàng */
    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    /** Ngày yêu cầu giao hàng */
    @Column(name = "required_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requiredDate;

    /** Ngày thực tế giao hàng */
    @Column(name = "shipped_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date shippedDate;

    /**
     * Trạng thái đơn hàng.
     * Các giá trị hợp lệ: PENDING, CONFIRMED, SHIPPED, DONE, CANCELLED
     */
    @Column(length = 50)
    private String status = "PENDING";

    /** Ghi chú của khách hàng */
    @Column(length = 255)
    private String comments;

    /**
     * Khách hàng đặt đơn hàng này.
     * Quan hệ Many-to-One: nhiều đơn hàng thuộc 1 khách hàng.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    public Date getOrderDate() { return this.orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public Date getRequiredDate() { return this.requiredDate; }
    public void setRequiredDate(Date requiredDate) { this.requiredDate = requiredDate; }
    public Date getShippedDate() { return this.shippedDate; }
    public void setShippedDate(Date shippedDate) { this.shippedDate = shippedDate; }
    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }
    public String getComments() { return this.comments; }
    public void setComments(String comments) { this.comments = comments; }
    public Customer getCustomer() { return this.customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }


}
