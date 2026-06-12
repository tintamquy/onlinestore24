package com.thannong.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Cấu hình CORS (Cross-Origin Resource Sharing).
 * Cho phép frontend (chạy trên Cloudflare Pages hoặc localhost:5500)
 * gọi API đến backend (localhost:8080).
 */
@Configuration
public class CorsConfig {

    /**
     * Tạo CorsFilter cho phép request từ các origin được liệt kê.
     * Khi deploy production, thay "*" bằng domain thật của frontend.
     *
     * @return CorsFilter đã được cấu hình
     */
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Cho phép tất cả origin trong môi trường phát triển
        // Production: thay bằng "https://your-shop.pages.dev"
        config.setAllowedOriginPatterns(List.of("*"));

        // Cho phép các HTTP method cần thiết
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Cho phép tất cả header (bao gồm Authorization cho JWT)
        config.setAllowedHeaders(List.of("*"));

        // Cho phép gửi credentials (cookie, Authorization header)
        config.setAllowCredentials(true);

        // Áp dụng cấu hình cho toàn bộ đường dẫn API
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
