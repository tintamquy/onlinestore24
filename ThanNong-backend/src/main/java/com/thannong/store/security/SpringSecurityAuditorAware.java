package com.thannong.store.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Cung cấp ID người dùng hiện tại cho JPA Auditing.
 * Được dùng để tự động điền createdBy và updatedBy trong BaseEntity.
 */
@Component("springSecurityAuditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    /**
     * Lấy ID của người dùng đang đăng nhập.
     * Nếu chưa đăng nhập (anonymous) thì trả về 0L.
     *
     * @return Optional chứa userId hoặc Optional.of(0L)
     */
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(0L);
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return Optional.of(((UserPrincipal) principal).getUserId());
        }
        return Optional.of(0L);
    }

}
