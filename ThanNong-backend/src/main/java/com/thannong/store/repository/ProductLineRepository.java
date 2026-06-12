package com.thannong.store.repository;

import com.thannong.store.entity.ProductLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository truy vấn bảng product_lines. */
@Repository
public interface ProductLineRepository extends JpaRepository<ProductLine, Integer> {
}
