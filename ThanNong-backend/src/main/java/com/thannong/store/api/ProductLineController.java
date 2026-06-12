package com.thannong.store.api;

import com.thannong.store.entity.ProductLine;
import com.thannong.store.repository.ProductLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý dòng sản phẩm (danh mục).
 * GET: PUBLIC, POST/PUT/DELETE: ADMIN
 */
@RestController
@RequestMapping("/api/product-lines")
public class ProductLineController {

    @Autowired
    private ProductLineRepository productLineRepository;

    /** Lấy tất cả danh mục sản phẩm (dùng cho dropdown filter) */
    @GetMapping
    public ResponseEntity<List<ProductLine>> getAllProductLines() {
        return ResponseEntity.ok(productLineRepository.findAll());
    }

    /** Thêm danh mục mới (chỉ ADMIN) */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductLine> createProductLine(@RequestBody ProductLine productLine) {
        productLineRepository.save(productLine);
        return ResponseEntity.ok(productLine);
    }

    /** Xóa danh mục (chỉ ADMIN) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProductLine(@PathVariable Integer id) {
        productLineRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa danh mục");
    }

}
