package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entity tương ứng bảng product_lines (dòng sản phẩm / danh mục).
 * Ví dụ: "Chăm sóc tóc", "Dưỡng da", "Vệ sinh cá nhân"...
 */
@Entity
@Table(name = "product_lines")
@Getter
@Setter
public class ProductLine {

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Tên dòng sản phẩm */
    @Column(name = "product_line", length = 50, nullable = false)
    private String productLine;

    /** Mô tả dòng sản phẩm */
    @Column(length = 2500)
    private String description;

}
