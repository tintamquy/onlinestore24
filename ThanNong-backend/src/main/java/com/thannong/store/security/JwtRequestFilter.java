package com.thannong.store.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter chạy 1 lần mỗi request để kiểm tra JWT token.
 * Quy trình:
 * 1. Đọc header Authorization: Bearer <token>
 * 2. Xác thực token bằng JwtUtil
 * 3. Nếu hợp lệ → đặt Authentication vào SecurityContext
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Đọc header Authorization
        String authHeader = request.getHeader("Authorization");

        String token    = null;
        String username = null;

        // Kiểm tra header có đúng định dạng "Bearer <token>" không
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            token    = authHeader.substring(BEARER_PREFIX.length());
            username = jwtUtil.getUsernameFromToken(token);
        }

        // Nếu token hợp lệ và SecurityContext chưa có authentication
        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null
                && jwtUtil.validateToken(token)) {

            // Load UserDetails từ username
            UserPrincipal userPrincipal =
                    (UserPrincipal) userDetailsService.loadUserByUsername(username);

            // Tạo authentication và đặt vào SecurityContext
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userPrincipal,
                            null,
                            userPrincipal.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Tiếp tục filter chain
        filterChain.doFilter(request, response);
    }

}
