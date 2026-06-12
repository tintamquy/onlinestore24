package com.thannong.store.api;

import com.thannong.store.dto.LoginRequestDto;
import com.thannong.store.dto.LoginResponseDto;
import com.thannong.store.entity.Token;
import com.thannong.store.repository.TokenRepository;
import com.thannong.store.security.JwtUtil;
import com.thannong.store.security.UserPrincipal;
import com.thannong.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * Controller xử lý đăng ký và đăng nhập.
 * Các endpoint này không yêu cầu xác thực JWT (xem SecurityConfig).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Đăng ký tài khoản mới.
     * Role mặc định: ROLE_CUSTOMER.
     *
     * @param request DTO chứa username và password
     * @return thông điệp thành công hoặc lỗi
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequestDto request) {
        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String result = userService.registerUser(request.getUsername(), encodedPassword);

        if ("USERNAME_EXISTED".equals(result)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tên đăng nhập đã tồn tại, vui lòng chọn tên khác");
        }
        return ResponseEntity.ok("Đăng ký thành công! Bạn có thể đăng nhập ngay.");
    }

    /**
     * Đăng nhập và nhận JWT token.
     *
     * @param request DTO chứa username và password
     * @return LoginResponseDto gồm token + thông tin user
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        // Load user từ DB
        UserPrincipal userPrincipal;
        try {
            userPrincipal = userService.findByUsername(request.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Tài khoản không tồn tại");
        }

        // So sánh mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), userPrincipal.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Mật khẩu không chính xác");
        }

        // Tạo JWT token
        String jwtToken = jwtUtil.generateToken(userPrincipal);

        // Lưu token vào DB (để tracking)
        Token tokenEntity = new Token();
        tokenEntity.setToken(jwtToken);
        tokenEntity.setTokenExpDate(jwtUtil.generateExpirationDate());
        tokenEntity.setCreatedBy(userPrincipal.getUserId());
        tokenEntity.setCreatedAt(new Date());
        tokenRepository.save(tokenEntity);

        // Danh sách role dạng String
        java.util.List<String> roleNames = userPrincipal.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        // TODO: Lấy customerId nếu là ROLE_CUSTOMER (cần thêm vào User entity)
        LoginResponseDto response = new LoginResponseDto(
                jwtToken,
                userPrincipal.getUsername(),
                roleNames,
                null  // customerId — cần query từ User.customerId
        );

        return ResponseEntity.ok(response);
    }

}
