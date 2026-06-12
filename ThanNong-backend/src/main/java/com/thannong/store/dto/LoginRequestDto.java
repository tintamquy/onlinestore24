package com.thannong.store.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO nhận dữ liệu đăng nhập từ client.
 * Không bao giờ trả DTO này ra ngoài response.
 */
@Getter
@Setter
public class LoginRequestDto {
    /** Tên đăng nhập */
    private String username;
    /** Mật khẩu chưa mã hóa */
    private String password;
}
