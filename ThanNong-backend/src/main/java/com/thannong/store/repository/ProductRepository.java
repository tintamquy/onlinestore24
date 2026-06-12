package com.thannong.store.repository;

import com.thannong.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository truy vấn bảng products.
 * Hỗ trợ phân trang và filter theo tên, giá, dòng SP.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Tìm kiếm sản phẩm theo nhiều tiêu chí, phân trang.
     * Bỏ qua các SP đã bị soft delete (deleted = false).
     *
     * @param keyword     từ khóa tìm trong tên SP (null = tất cả)
     * @param minPrice    giá tối thiểu (null = không giới hạn)
     * @param maxPrice    giá tối đa (null = không giới hạn)
     * @param productLineId ID dòng SP (null = tất cả)
     * @param pageable    thông tin phân trang
     * @return Page<Product>
     */
    @Query("SELECT p FROM Product p WHERE p.deleted = false " +
           "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:minPrice IS NULL OR p.buyPrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.buyPrice <= :maxPrice) " +
           "AND (:productLineId IS NULL OR p.productLine.id = :productLineId)")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("productLineId") Integer productLineId,
            Pageable pageable
    );

}
