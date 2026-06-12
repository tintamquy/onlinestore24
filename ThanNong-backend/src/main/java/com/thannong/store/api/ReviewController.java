package com.thannong.store.api;

import com.thannong.store.dto.ReviewDto;
import com.thannong.store.entity.Customer;
import com.thannong.store.entity.Product;
import com.thannong.store.entity.Review;
import com.thannong.store.repository.CustomerRepository;
import com.thannong.store.repository.ProductRepository;
import com.thannong.store.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller quản lý đánh giá sản phẩm.
 * - GET /api/reviews/product/{productId}: PUBLIC — xem review SP
 * - POST /api/reviews: CUSTOMER — thêm review
 * - DELETE /api/reviews/{id}: ADMIN — xóa review vi phạm
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Lấy danh sách review của một sản phẩm.
     *
     * @param productId ID sản phẩm
     * @return danh sách ReviewDto
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByProduct(
            @PathVariable Integer productId) {
        List<ReviewDto> reviews = reviewRepository
                .findByProductIdAndDeletedFalse(productId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviews);
    }

    /**
     * Lấy tất cả review (Admin quản lý).
     *
     * @return danh sách tất cả review
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> reviews = reviewRepository.findAll()
                .stream()
                .filter(r -> !r.isDeleted())
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviews);
    }

    /**
     * Khách hàng thêm đánh giá sản phẩm đã mua.
     * Task 5 (rating) + Task 6 (review/comment).
     *
     * @param reviewDto DTO chứa customerId, productId, rating, comment
     * @return ReviewDto đã lưu
     */
    @Autowired
    private com.thannong.store.repository.UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> addReview(@RequestBody ReviewDto reviewDto) {
        // Lấy thông tin user đang đăng nhập
        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof com.thannong.store.security.UserPrincipal)) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }
        com.thannong.store.security.UserPrincipal userPrincipal = (com.thannong.store.security.UserPrincipal) principal;

        com.thannong.store.entity.User user = userRepository.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        Customer customer;
        if (user.getCustomerId() == null) {
            // Chưa từng mua hàng -> tạo Customer ảo để review
            customer = new Customer();
            customer.setFirstName(user.getUsername());
            customer.setLastName("");
            customer = customerRepository.save(customer);

            user.setCustomerId(customer.getId());
            userRepository.save(user);
        } else {
            customer = customerRepository.findById(user.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        }

        // Kiểm tra khách hàng đã review SP này chưa
        if (reviewRepository.existsByCustomerIdAndProductId(
                customer.getId(), reviewDto.getProductId())) {
            return ResponseEntity.badRequest()
                    .body("Bạn đã đánh giá sản phẩm này rồi");
        }

        // Validate rating (1-5 sao)
        if (reviewDto.getRating() < 1 || reviewDto.getRating() > 5) {
            return ResponseEntity.badRequest()
                    .body("Rating phải trong khoảng 1 đến 5 sao");
        }

        Product product = productRepository.findById(reviewDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        Review review = new Review();
        review.setCustomer(customer);
        review.setProduct(product);
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setCreatedAt(new Date());

        reviewRepository.save(review);
        return ResponseEntity.ok(mapToDto(review));
    }

    /**
     * Admin xóa review vi phạm (Task 7).
     * Dùng soft delete: deleted = true.
     *
     * @param id ID review cần xóa
     * @return thông điệp kết quả
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy review ID: " + id));
        review.setDeleted(true);
        reviewRepository.save(review);
        return ResponseEntity.ok("Đã xóa review thành công");
    }

    /** Chuyển Review entity → ReviewDto */
    private ReviewDto mapToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        if (review.getCustomer() != null) {
            dto.setCustomerId(review.getCustomer().getId());
            dto.setCustomerName(
                review.getCustomer().getFirstName() + " " +
                review.getCustomer().getLastName()
            );
        }
        if (review.getProduct() != null) {
            dto.setProductId(review.getProduct().getId());
            dto.setProductName(review.getProduct().getProductName());
        }
        return dto;
    }

}
