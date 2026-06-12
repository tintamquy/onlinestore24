package com.thannong.store.repository;

import com.thannong.store.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository truy vấn bảng t_review. */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Lấy tất cả review của một sản phẩm (chưa bị xóa).
     * @param productId ID sản phẩm
     * @return danh sách review
     */
    List<Review> findByProductIdAndDeletedFalse(Integer productId);

    /**
     * Kiểm tra khách hàng đã review sản phẩm này chưa.
     * @param customerId ID khách hàng
     * @param productId  ID sản phẩm
     * @return true nếu đã review
     */
    boolean existsByCustomerIdAndProductId(Integer customerId, Integer productId);

    @org.springframework.data.jpa.repository.Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.deleted = false")
    Double getAverageRatingByProductId(@org.springframework.data.repository.query.Param("productId") Integer productId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.deleted = false")
    Long countReviewsByProductId(@org.springframework.data.repository.query.Param("productId") Integer productId);
}
