package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entity tương ứng bảng t_role (vai trò người dùng).
 * Các vai trò hiện có: ROLE_ADMIN, ROLE_CUSTOMER
 */
@Entity
@Table(name = "t_role")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tên vai trò. Ví dụ: ROLE_ADMIN, ROLE_CUSTOMER */
    @Column(length = 50, nullable = false)
    private String name;

}
