package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entity tương ứng bảng offices (văn phòng chi nhánh).
 * Bảng này là gốc của chuỗi quan hệ: offices → employees → customers.
 */
@Entity
@Table(name = "offices")
@Getter
@Setter
public class Office {

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Tên thành phố đặt văn phòng */
    @Column(length = 50)
    private String city;

    /** Số điện thoại văn phòng */
    @Column(length = 50)
    private String phone;

    /** Địa chỉ văn phòng */
    @Column(length = 255)
    private String addressLine;

    /** Tỉnh/Bang */
    @Column(length = 50)
    private String state;

    /** Quốc gia */
    @Column(length = 50)
    private String country;

    /** Khu vực hoạt động */
    @Column(length = 50)
    private String territory;

}
