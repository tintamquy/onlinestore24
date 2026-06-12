package com.thannong.store.repository;

import com.thannong.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository truy vấn bảng t_user.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Tìm user theo tên đăng nhập.
     * @param username tên đăng nhập
     * @return Optional<User>
     */
    Optional<User> findByUsername(String username);

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa.
     * @param username tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại
     */
    boolean existsByUsername(String username);

}
