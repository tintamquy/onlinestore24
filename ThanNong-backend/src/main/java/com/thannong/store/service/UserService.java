package com.thannong.store.service;

import com.thannong.store.security.UserPrincipal;

/**
 * Service xử lý nghiệp vụ người dùng: tìm kiếm, tạo tài khoản.
 */
public interface UserService {

    /**
     * Tìm UserPrincipal theo tên đăng nhập.
     * Ném UsernameNotFoundException nếu không tìm thấy.
     *
     * @param username tên đăng nhập
     * @return UserPrincipal đã load từ DB
     */
    UserPrincipal findByUsername(String username);

    /**
     * Tạo tài khoản người dùng mới.
     * Gán role ROLE_CUSTOMER mặc định.
     *
     * @param username tên đăng nhập
     * @param password mật khẩu chưa mã hóa
     * @return thông điệp kết quả
     */
    String registerUser(String username, String password);

}
