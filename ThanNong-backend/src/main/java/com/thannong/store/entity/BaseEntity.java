package com.thannong.store.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Lớp cha trừu tượng cho tất cả Entity.
 * Chứa các trường audit chung: id, deleted, createdAt, updatedAt, createdBy, updatedBy.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Khóa chính tự tăng */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Soft delete: đánh dấu xóa thay vì xóa thật sự */
    @Column(columnDefinition = "boolean default false")
    private boolean deleted = false;

    /** Thời điểm tạo bản ghi (tự động bởi JPA Auditing) */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /** Thời điểm cập nhật cuối (tự động bởi JPA Auditing) */
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    /** ID người tạo bản ghi */
    @CreatedBy
    private Long createdBy;

    /** ID người cập nhật cuối */
    @LastModifiedBy
    private Long updatedBy;

}
