package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entity tương ứng bảng employees (nhân viên / quản lý).
 * Nhân viên thuộc một văn phòng (office) và có thể báo cáo lên manager (report_to).
 * Đây cũng là người phụ trách bán hàng (sales_rep) được gán cho khách hàng.
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee {

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Họ nhân viên */
    @Column(name = "last_name", length = 50)
    private String lastName;

    /** Tên nhân viên */
    @Column(name = "first_name", length = 50)
    private String firstName;

    /** Số máy nhánh nội bộ */
    @Column(length = 50)
    private String extension;

    /** Email công ty */
    @Column(length = 50)
    private String email;

    /**
     * Văn phòng mà nhân viên làm việc.
     * Quan hệ Many-to-One: nhiều nhân viên thuộc 1 văn phòng.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_code")
    private Office office;

    /**
     * ID của quản lý trực tiếp (tự tham chiếu trong bảng employees).
     * Ví dụ: nhân viên bình thường report_to = ID của trưởng phòng.
     */
    @Column(name = "report_to")
    private Integer reportTo;

    /** Chức danh công việc */
    @Column(name = "job_title", length = 50)
    private String jobTitle;

}
