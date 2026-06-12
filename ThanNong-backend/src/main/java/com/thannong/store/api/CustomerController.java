package com.thannong.store.api;

import com.thannong.store.entity.Customer;
import com.thannong.store.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý khách hàng (chỉ ADMIN).
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Lấy danh sách khách hàng có phân trang và tìm kiếm.
     *
     * @param page    trang hiện tại (0-indexed)
     * @param size    số KH mỗi trang
     * @param keyword từ khóa tìm theo tên / SĐT
     * @return Map chứa danh sách KH + thông tin phân trang
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCustomers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {

        Page<Customer> customerPage = customerRepository.searchCustomers(
                keyword, PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("data",        customerPage.getContent());
        response.put("totalItems",  customerPage.getTotalElements());
        response.put("totalPages",  customerPage.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy chi tiết 1 khách hàng.
     *
     * @param id ID khách hàng
     * @return Customer entity
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + id));
        return ResponseEntity.ok(customer);
    }

    /**
     * Thêm khách hàng mới (Admin).
     *
     * @param customer entity khách hàng
     * @return khách hàng đã lưu
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        customerRepository.save(customer);
        return ResponseEntity.ok(customer);
    }

    /**
     * Cập nhật thông tin khách hàng (Admin).
     *
     * @param id       ID khách hàng
     * @param customer thông tin mới
     * @return khách hàng đã cập nhật
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer id,
                                                    @RequestBody Customer customer) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + id));

        existing.setFirstName(customer.getFirstName());
        existing.setLastName(customer.getLastName());
        existing.setPhoneNumber(customer.getPhoneNumber());
        existing.setAddress(customer.getAddress());
        existing.setCity(customer.getCity());
        existing.setState(customer.getState());
        existing.setPostalCode(customer.getPostalCode());
        existing.setCountry(customer.getCountry());
        existing.setCreditLimit(customer.getCreditLimit());

        customerRepository.save(existing);
        return ResponseEntity.ok(existing);
    }

}
