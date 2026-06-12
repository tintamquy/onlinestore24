package com.thannong.store.service.impl;

import com.thannong.store.entity.Role;
import com.thannong.store.entity.User;
import com.thannong.store.repository.UserRepository;
import com.thannong.store.security.UserPrincipal;
import com.thannong.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collections;

/**
 * Triển khai UserService và UserDetailsService.
 * UserDetailsService là interface bắt buộc của Spring Security
 * để load user theo username khi xác thực.
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     * Được Spring Security gọi tự động khi cần load user.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy tài khoản với username: " + username));
        return new UserPrincipal(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserPrincipal findByUsername(String username) {
        return (UserPrincipal) loadUserByUsername(username);
    }

    /**
     * {@inheritDoc}
     * Tìm role ROLE_CUSTOMER trong DB và gán cho user mới.
     */
    @Override
    @Transactional
    public String registerUser(String username, String encodedPassword) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(username)) {
            return "USERNAME_EXISTED";
        }

        // Tìm role ROLE_CUSTOMER trong DB
        Role customerRole = entityManager
                .createQuery("SELECT r FROM Role r WHERE r.name = 'ROLE_CUSTOMER'", Role.class)
                .getSingleResult();

        // Tạo user mới
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        newUser.setRoles(Collections.singleton(customerRole));

        userRepository.save(newUser);
        return "SUCCESS";
    }

}
