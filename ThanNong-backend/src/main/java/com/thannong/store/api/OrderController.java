package com.thannong.store.api;

import com.thannong.store.dto.OrderDto;
import com.thannong.store.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller quản lý đơn hàng.
 * - POST /api/orders: PUBLIC — khách checkout không cần đăng nhập
 * - GET /api/orders: chỉ ADMIN
 * - GET /api/orders/my-orders: khách hàng xem đơn của mình (cần đăng nhập)
 * - PUT /api/orders/{id}/status: chỉ ADMIN cập nhật trạng thái
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    /**
     * Tạo đơn hàng mới khi khách checkout.
     * PUBLIC: không cần JWT token.
     *
     * @param orderDto thông tin đơn hàng + khách hàng
     * @return OrderDto đã tạo (gồm ID đơn hàng)
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        OrderDto savedOrder = orderService.createOrder(orderDto);
        return ResponseEntity.ok(savedOrder);
    }

    /**
     * Admin xem toàn bộ đơn hàng.
     *
     * @return danh sách OrderDto
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Xem chi tiết 1 đơn hàng theo ID.
     *
     * @param id ID đơn hàng
     * @return OrderDto
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * Khách hàng xem danh sách đơn hàng của mình.
     *
     * @param customerId ID khách hàng
     * @return danh sách OrderDto của khách
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomer(
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    /**
     * Khách hàng tra cứu đơn hàng qua số điện thoại.
     * PUBLIC: Không yêu cầu JWT (do frontend cho phép nhập SĐT).
     *
     * @param phone Số điện thoại khách hàng
     * @return danh sách OrderDto
     */
    @GetMapping("/by-phone")
    public ResponseEntity<List<OrderDto>> getOrdersByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(orderService.getOrdersByPhone(phone));
    }

    /**
     * Admin cập nhật trạng thái đơn hàng.
     *
     * @param id    ID đơn hàng
     * @param body  Map chứa key "status" với giá trị mới
     * @return OrderDto đã cập nhật
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        return ResponseEntity.ok(orderService.updateOrderStatus(id, newStatus));
    }

}
