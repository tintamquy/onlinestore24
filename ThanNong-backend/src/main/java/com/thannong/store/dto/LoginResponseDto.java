package com.thannong.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO trả về sau khi đăng nhập thành công.
 * Chứa JWT token và thông tin cơ bản của user.
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    /** JWT access token */
    private String token;
    /** Tên đăng nhập */
    private String username;
    /** Danh sách vai trò: ["ROLE_ADMIN"] hoặc ["ROLE_CUSTOMER"] */
    private List<String> roles;
    /** ID khách hàng liên kết (null nếu là ADMIN hoặc chưa có) */
    private Integer customerId;
    /** ID của user đăng nhập */
    private Long userId;
}
