package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity tương ứng bảng t_token (JWT token đang hoạt động).
 * Lưu token để hỗ trợ logout (blacklist token) và kiểm tra hết hạn.
 */
@Entity
@Table(name = "t_token")
@Getter
@Setter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** JWT token string */
    @Column(columnDefinition = "TEXT")
    private String token;

    /** Thời điểm token hết hạn */
    @Column(name = "token_exp_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenExpDate;

    /** ID user tạo token này */
    @Column(name = "created_by")
    private Long createdBy;

    /** Thời điểm tạo token */
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

}
