package com.thannong.store.security;

import com.thannong.store.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Wrapper class bọc entity User thành UserDetails của Spring Security.
 * Spring Security dùng class này để kiểm tra quyền và xác thực.
 */
@Getter
public class UserPrincipal implements UserDetails {

    /** ID người dùng trong DB */
    private final Long userId;

    /** Tên đăng nhập */
    private final String username;

    /** Mật khẩu đã mã hóa */
    private final String password;

    /** Danh sách quyền được chuyển từ Role */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Khởi tạo UserPrincipal từ entity User.
     * Chuyển đổi Set<Role> → Collection<GrantedAuthority>.
     *
     * @param user entity User từ database
     */
    public UserPrincipal(User user) {
        this.userId   = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        // Chuyển mỗi role.name → SimpleGrantedAuthority
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Các thuộc tính tài khoản — luôn true trong dự án demo
    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()            { return true; }

}
