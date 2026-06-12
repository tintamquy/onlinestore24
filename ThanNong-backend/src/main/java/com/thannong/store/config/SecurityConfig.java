package com.thannong.store.config;

import com.thannong.store.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Cấu hình Spring Security cho toàn bộ ứng dụng.
 * - Tắt CSRF (dùng JWT thay thế)
 * - Dùng Stateless session (JWT)
 * - Cấu hình các đường dẫn public và cần xác thực
 * - Thêm JwtRequestFilter trước UsernamePasswordAuthenticationFilter
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * Bean mã hóa mật khẩu bằng BCrypt.
     * Được dùng trong AuthController khi đăng ký và đăng nhập.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // Bật CORS cho Spring Security
            .cors().and()
            // Tắt CSRF vì dùng JWT (stateless API)
            .csrf().disable()

            // Cấu hình phân quyền đường dẫn
            .authorizeRequests()
                // API công khai: không cần đăng nhập
                .antMatchers(
                    "/api/auth/**",         // đăng nhập, đăng ký
                    "/api/products/**",     // xem sản phẩm
                    "/api/product-lines/**",// xem danh mục
                    "/api/reviews/product/**", // xem review SP
                    "/api/orders",          // tạo đơn hàng (checkout)
                    "/api/orders/by-phone", // tra cứu đơn hàng qua SĐT
                    "/api/images/**"        // xem ảnh sản phẩm
                ).permitAll()
                // Tất cả đường dẫn khác cần đăng nhập
                .anyRequest().authenticated()
            .and()

            // Dùng Stateless session: không lưu session phía server
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            // Thêm JWT filter trước filter xác thực mặc định của Spring
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
