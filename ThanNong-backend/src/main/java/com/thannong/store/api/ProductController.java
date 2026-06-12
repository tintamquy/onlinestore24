package com.thannong.store.api;

import com.thannong.store.dto.ProductDto;
import com.thannong.store.entity.Product;
import com.thannong.store.entity.ProductLine;
import com.thannong.store.repository.ProductLineRepository;
import com.thannong.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller quản lý sản phẩm.
 * - GET (danh sách, chi tiết): PUBLIC — không cần đăng nhập
 * - POST, PUT, DELETE: chỉ ADMIN
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductLineRepository productLineRepository;

    /** Thư mục lưu ảnh upload (cấu hình trong application.properties) */
    @Value("${app.upload.dir:uploads/products}")
    private String uploadDir;

    /**
     * Lấy danh sách sản phẩm có phân trang và filter.
     *
     * @param page          trang hiện tại (0-indexed, mặc định 0)
     * @param size          số SP mỗi trang (mặc định 12)
     * @param keyword       từ khóa tìm kiếm theo tên
     * @param minPrice      giá tối thiểu
     * @param maxPrice      giá tối đa
     * @param productLineId ID dòng SP (danh mục)
     * @param sortBy        sắp xếp theo: "price_asc", "price_desc", "name"
     * @return Map chứa data (danh sách SP) và thông tin phân trang
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer productLineId,
            @RequestParam(defaultValue = "id") String sortBy) {

        // Xác định cách sắp xếp
        Sort sort;
        switch (sortBy) {
            case "price_asc":  sort = Sort.by("buyPrice").ascending();  break;
            case "price_desc": sort = Sort.by("buyPrice").descending(); break;
            case "name":       sort = Sort.by("productName").ascending(); break;
            default:           sort = Sort.by("id").descending();
        }

        Page<Product> productPage = productRepository.searchProducts(
                keyword, minPrice, maxPrice, productLineId,
                PageRequest.of(page, size, sort)
        );

        // Map entity → DTO
        List<ProductDto> productDtos = productPage.getContent()
                .stream().map(this::mapToDto).collect(Collectors.toList());

        // Đóng gói response kèm thông tin phân trang
        Map<String, Object> response = new HashMap<>();
        response.put("data",       productDtos);
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy chi tiết 1 sản phẩm theo ID.
     *
     * @param id ID sản phẩm
     * @return ProductDto hoặc 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Integer id) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + id));
        return ResponseEntity.ok(mapToDto(product));
    }

    /**
     * Thêm sản phẩm mới (chỉ ADMIN).
     * Nhận JSON body chứa thông tin SP.
     *
     * @param dto DTO thông tin sản phẩm
     * @return ProductDto đã lưu
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto dto) {
        Product product = mapToEntity(dto);
        productRepository.save(product);
        return ResponseEntity.ok(mapToDto(product));
    }

    /**
     * Upload ảnh sản phẩm (chỉ ADMIN).
     * Lưu file vào thư mục uploads/products/ trên server.
     *
     * @param file MultipartFile ảnh cần upload
     * @return Map chứa fileName và imageUrl
     */
    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        // Tạo thư mục nếu chưa có
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Lưu file với tên gốc (hoặc có thể thêm timestamp để tránh trùng)
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), uploadPath.resolve(fileName));

        Map<String, String> result = new HashMap<>();
        result.put("fileName",  fileName);
        result.put("imageUrl",  fileName);
        return ResponseEntity.ok(result);
    }

    /**
     * Cập nhật sản phẩm (chỉ ADMIN).
     *
     * @param id  ID sản phẩm cần cập nhật
     * @param dto DTO thông tin mới
     * @return ProductDto đã cập nhật
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Integer id,
                                                     @RequestBody ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + id));

        product.setProductCode(dto.getProductCode());
        product.setProductName(dto.getProductName());
        product.setProductDescription(dto.getProductDescription());
        product.setProductScale(dto.getProductScale());
        product.setProductVendor(dto.getProductVendor());
        product.setQuantityInStock(dto.getQuantityInStock());
        product.setBuyPrice(dto.getBuyPrice());
        if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
        }
        if (dto.getProductLineId() != null) {
            productLineRepository.findById(dto.getProductLineId())
                    .ifPresent(product::setProductLine);
        }

        productRepository.save(product);
        return ResponseEntity.ok(mapToDto(product));
    }

    /**
     * Xóa sản phẩm (soft delete, chỉ ADMIN).
     * Đặt deleted = true thay vì xóa khỏi DB.
     *
     * @param id ID sản phẩm cần xóa
     * @return thông điệp kết quả
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + id));
        product.setDeleted(true);
        productRepository.save(product);
        return ResponseEntity.ok("Đã xóa sản phẩm thành công");
    }

    // ========== Mapper helpers ==========

    /** Chuyển Product entity → ProductDto */
    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setProductCode(product.getProductCode());
        dto.setProductName(product.getProductName());
        dto.setProductDescription(product.getProductDescription());
        dto.setProductScale(product.getProductScale());
        dto.setProductVendor(product.getProductVendor());
        dto.setQuantityInStock(product.getQuantityInStock());
        dto.setBuyPrice(product.getBuyPrice());
        dto.setImageUrl(product.getImageUrl());
        if (product.getProductLine() != null) {
            dto.setProductLineId(product.getProductLine().getId());
            dto.setProductLineName(product.getProductLine().getProductLine());
        }
        return dto;
    }

    /** Chuyển ProductDto → Product entity (khi tạo mới) */
    private Product mapToEntity(ProductDto dto) {
        Product product = new Product();
        product.setProductCode(dto.getProductCode());
        product.setProductName(dto.getProductName());
        product.setProductDescription(dto.getProductDescription());
        product.setProductScale(dto.getProductScale());
        product.setProductVendor(dto.getProductVendor());
        product.setQuantityInStock(dto.getQuantityInStock() != null ? dto.getQuantityInStock() : 0);
        product.setBuyPrice(dto.getBuyPrice());
        product.setImageUrl(dto.getImageUrl());
        if (dto.getProductLineId() != null) {
            productLineRepository.findById(dto.getProductLineId())
                    .ifPresent(product::setProductLine);
        }
        return product;
    }

}
