package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity tương ứng bảng t_user (tài khoản đăng nhập hệ thống).
 * Dùng cho cả Admin và Customer đăng nhập.
 * Không nên nhầm lẫn với bảng customers (thông tin mua hàng).
 */
@Entity
@Table(name = "t_user")
@Getter
@Setter
public class User extends BaseEntity {

    /** Tên đăng nhập (email hoặc username) */
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /** Mật khẩu đã mã hóa bằng BCrypt */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Danh sách vai trò của user.
     * Quan hệ Many-to-Many qua bảng trung gian t_user_role.
     */
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(
        name = "t_user_role",
        joinColumns        = { @JoinColumn(name = "user_id") },
        inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * ID khách hàng liên kết (nếu là tài khoản khách hàng).
     * Null nếu là tài khoản ADMIN/EMPLOYEE.
     */
    @Column(name = "customer_id")
    private Integer customerId;

}
