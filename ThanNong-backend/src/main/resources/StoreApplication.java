package com.thannong.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Điểm khởi đầu của ứng dụng Spring Boot — Shop Thần Nông.
 * Chạy lệnh: mvn spring-boot:run
 * API gốc:   http://localhost:8080/api/
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class StoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
    }

}
