package com.thannong.store.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Tiện ích JWT: tạo, xác thực và trích xuất thông tin từ JWT token.
 * Sử dụng thư viện nimbus-jose-jwt với thuật toán HMAC-SHA256.
 */
@Component
public class JwtUtil {

    /** Secret key để ký JWT — phải dài ít nhất 32 ký tự */
    private static final String SECRET_KEY = "ThanNong2024SecretKeyForJwtTokenGeneration";

    /** Thời gian sống của token: 24 giờ (mili giây) */
    private static final long TOKEN_VALIDITY_MS = 24 * 60 * 60 * 1000L;

    /**
     * Tạo JWT token từ thông tin UserPrincipal.
     * Claims chứa: username và userId.
     *
     * @param userPrincipal thông tin người dùng đã xác thực
     * @return chuỗi JWT token
     */
    public String generateToken(UserPrincipal userPrincipal) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userPrincipal.getUsername())
                    .claim("userId", userPrincipal.getUserId())
                    .issueTime(new Date())
                    .expirationTime(generateExpirationDate())
                    .build();

            SignedJWT signedJwt = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJwt.sign(new MACSigner(SECRET_KEY.getBytes()));
            return signedJwt.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Lỗi tạo JWT token: " + e.getMessage(), e);
        }
    }

    /**
     * Tính thời điểm hết hạn của token (hiện tại + TOKEN_VALIDITY_MS).
     *
     * @return Date thời điểm token hết hạn
     */
    public Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + TOKEN_VALIDITY_MS);
    }

    /**
     * Xác thực JWT token: kiểm tra chữ ký và thời hạn.
     *
     * @param token chuỗi JWT cần kiểm tra
     * @return true nếu token hợp lệ và chưa hết hạn
     */
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            // Kiểm tra chữ ký
            if (!signedJwt.verify(new MACVerifier(SECRET_KEY.getBytes()))) {
                return false;
            }
            // Kiểm tra hạn sử dụng
            Date expirationTime = signedJwt.getJWTClaimsSet().getExpirationTime();
            return expirationTime != null && expirationTime.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Trích xuất username từ JWT token.
     *
     * @param token chuỗi JWT
     * @return username của người dùng, hoặc null nếu lỗi
     */
    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            return signedJwt.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}
