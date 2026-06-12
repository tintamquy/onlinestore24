package com.thannong.store.service.impl;

import com.thannong.store.dto.OrderDetailDto;
import com.thannong.store.dto.OrderDto;
import com.thannong.store.entity.*;
import com.thannong.store.repository.CustomerRepository;
import com.thannong.store.repository.OrderDetailRepository;
import com.thannong.store.repository.OrderRepository;
import com.thannong.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Xử lý nghiệp vụ đơn hàng:
 * - Tạo đơn hàng mới (có xử lý SĐT trùng/mới)
 * - Lấy đơn hàng của khách hàng
 * - Admin cập nhật trạng thái đơn hàng
 */
@Service
public class OrderServiceImpl {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Tạo đơn hàng mới khi khách checkout.
     * Logic kiểm tra SĐT:
     * - Nếu SĐT đã có trong DB → cập nhật thông tin KH cũ, tạo order mới cho KH đó
     * - Nếu SĐT chưa có → tạo KH mới + order mới
     *
     * @param orderDto DTO chứa thông tin KH và danh sách sản phẩm
     * @return OrderDto đã lưu với ID
     */
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {

        // ===== Bước 1: Xử lý thông tin khách hàng theo SĐT =====
        Customer customer = customerRepository
                .findByPhoneNumber(orderDto.getCustomerPhone())
                .orElse(null);

        if (customer != null) {
            // SĐT đã tồn tại → cập nhật thông tin KH cũ
            customer.setFirstName(orderDto.getCustomerFirstName());
            customer.setLastName(orderDto.getCustomerLastName());
            customer.setAddress(orderDto.getCustomerAddress());
            customer.setCity(orderDto.getCustomerCity());
            customer.setCountry(orderDto.getCustomerCountry());
            customerRepository.save(customer);
        } else {
            // SĐT mới → tạo KH mới
            customer = new Customer();
            customer.setFirstName(orderDto.getCustomerFirstName());
            customer.setLastName(orderDto.getCustomerLastName());
            customer.setPhoneNumber(orderDto.getCustomerPhone());
            customer.setAddress(orderDto.getCustomerAddress());
            customer.setCity(orderDto.getCustomerCity());
            customer.setCountry(orderDto.getCustomerCountry());
            customer = customerRepository.save(customer);
        }

        // ===== Bước 2: Tạo đơn hàng =====
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(new Date());
        order.setStatus("PENDING");
        order.setComments(orderDto.getComments());
        order = orderRepository.save(order);

        // ===== Bước 3: Lưu từng dòng sản phẩm (order_detail) =====
        List<OrderDetailDto> savedDetailDtos = new ArrayList<>();

        for (OrderDetailDto detailDto : orderDto.getOrderDetails()) {
            Product product = productRepository.findById(detailDto.getProductId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy sản phẩm ID: " + detailDto.getProductId()));

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantityOrder(detailDto.getQuantityOrder());
            detail.setPriceEach(product.getBuyPrice()); // lưu giá tại thời điểm đặt
            orderDetailRepository.save(detail);

            // Map sang DTO để trả về
            detailDto.setId(detail.getId());
            detailDto.setOrderId(order.getId());
            detailDto.setPriceEach(detail.getPriceEach());
            detailDto.setSubTotal(detail.getPriceEach()
                    .multiply(BigDecimal.valueOf(detail.getQuantityOrder())));
            savedDetailDtos.add(detailDto);
        }

        // ===== Bước 4: Map Order entity → DTO để trả về =====
        return mapToOrderDto(order, customer, savedDetailDtos);
    }

    /**
     * Lấy danh sách tất cả đơn hàng (Admin).
     *
     * @return danh sách OrderDto
     */
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    List<OrderDetailDto> details = orderDetailRepository
                            .findByOrderId(order.getId())
                            .stream()
                            .map(this::mapToDetailDto)
                            .collect(Collectors.toList());
                    return mapToOrderDto(order, order.getCustomer(), details);
                })
                .collect(Collectors.toList());
    }

    /**
     * Lấy đơn hàng theo ID.
     *
     * @param orderId ID đơn hàng
     * @return OrderDto
     */
    public OrderDto getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));
        List<OrderDetailDto> details = orderDetailRepository.findByOrderId(orderId)
                .stream().map(this::mapToDetailDto).collect(Collectors.toList());
        return mapToOrderDto(order, order.getCustomer(), details);
    }

    /**
     * Lấy danh sách đơn hàng của một khách hàng.
     *
     * @param customerId ID khách hàng
     * @return danh sách OrderDto
     */
    public List<OrderDto> getOrdersByCustomerId(Integer customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(order -> {
                    List<OrderDetailDto> details = orderDetailRepository
                            .findByOrderId(order.getId())
                            .stream().map(this::mapToDetailDto).collect(Collectors.toList());
                    return mapToOrderDto(order, order.getCustomer(), details);
                })
                .collect(Collectors.toList());
    }

    /**
     * Tra cứu lịch sử đơn hàng qua số điện thoại.
     *
     * @param phone Số điện thoại
     * @return danh sách OrderDto
     */
    public List<OrderDto> getOrdersByPhone(String phone) {
        return orderRepository.findByCustomer_PhoneNumber(phone).stream()
                .map(order -> {
                    List<OrderDetailDto> details = orderDetailRepository
                            .findByOrderId(order.getId())
                            .stream().map(this::mapToDetailDto).collect(Collectors.toList());
                    return mapToOrderDto(order, order.getCustomer(), details);
                })
                .collect(Collectors.toList());
    }

    /**
     * Admin cập nhật trạng thái đơn hàng.
     *
     * @param orderId   ID đơn hàng
     * @param newStatus trạng thái mới (CONFIRMED, SHIPPED, DONE, CANCELLED)
     * @return OrderDto đã cập nhật
     */
    @Transactional
    public OrderDto updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));
        order.setStatus(newStatus);
        if ("SHIPPED".equals(newStatus)) {
            order.setShippedDate(new Date());
        }
        orderRepository.save(order);
        return getOrderById(orderId);
    }

    // ========== Mapper helpers ==========

    /** Chuyển Order entity + danh sách detail → OrderDto */
    private OrderDto mapToOrderDto(Order order, Customer customer, List<OrderDetailDto> details) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setRequiredDate(order.getRequiredDate());
        dto.setShippedDate(order.getShippedDate());
        dto.setStatus(order.getStatus());
        dto.setComments(order.getComments());
        dto.setOrderDetails(details);

        if (customer != null) {
            dto.setCustomerId(customer.getId());
            dto.setCustomerFirstName(customer.getFirstName());
            dto.setCustomerLastName(customer.getLastName());
            dto.setCustomerPhone(customer.getPhoneNumber());
            dto.setCustomerAddress(customer.getAddress());
            dto.setCustomerCity(customer.getCity());
            dto.setCustomerCountry(customer.getCountry());
        }

        // Tính tổng tiền đơn hàng
        BigDecimal totalAmount = details.stream()
                .map(d -> d.getPriceEach().multiply(BigDecimal.valueOf(d.getQuantityOrder())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(totalAmount);

        return dto;
    }

    /** Chuyển OrderDetail entity → OrderDetailDto */
    @Transactional
    public OrderDto updateOrder(Integer id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (orderDto.getStatus() != null) order.setStatus(orderDto.getStatus());
        if (orderDto.getComments() != null) order.setComments(orderDto.getComments());

        if (orderDto.getOrderDetails() != null && !orderDto.getOrderDetails().isEmpty()) {
            orderDetailRepository.deleteAll(order.getOrderDetails());
            order.getOrderDetails().clear();

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderDetailDto detailDto : orderDto.getOrderDetails()) {
                Product product = productRepository.findById(detailDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + detailDto.getProductId()));

                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                detail.setProduct(product);
                detail.setQuantityOrder(detailDto.getQuantityOrder());
                detail.setPriceEach(product.getBuyPrice());

                order.getOrderDetails().add(detail);
                orderDetailRepository.save(detail);

                BigDecimal subTotal = product.getBuyPrice()
                        .multiply(BigDecimal.valueOf(detailDto.getQuantityOrder()));
                totalAmount = totalAmount.add(subTotal);
            }
            order.setTotalAmount(totalAmount);
        }

        orderRepository.save(order);
        return mapToDto(order);
    }

    private OrderDetailDto mapToDetailDto(OrderDetail detail) {
        OrderDetailDto dto = new OrderDetailDto();
        dto.setId(detail.getId());
        dto.setOrderId(detail.getOrder().getId());
        dto.setProductId(detail.getProduct().getId());
        dto.setProductName(detail.getProduct().getProductName());
        dto.setProductImageUrl(detail.getProduct().getImageUrl());
        dto.setQuantityOrder(detail.getQuantityOrder());
        dto.setPriceEach(detail.getPriceEach());
        dto.setSubTotal(detail.getPriceEach()
                .multiply(BigDecimal.valueOf(detail.getQuantityOrder())));
        return dto;
    }

}
