package com.thannong.store.repository;

import com.thannong.store.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository truy vấn bảng customers.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Tìm khách hàng theo số điện thoại.
     * Dùng để kiểm tra khi checkout: SĐT đã có chưa?
     *
     * @param phoneNumber số điện thoại cần tìm
     * @return Optional<Customer>
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Tìm kiếm khách hàng theo tên, phân trang.
     *
     * @param keyword  từ khóa tên (null = tất cả)
     * @param pageable thông tin phân trang
     * @return Page<Customer>
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "(:keyword IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR c.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy tổng số tiền đã mua của tất cả khách hàng (dùng cho báo cáo VIP).
     * JOIN với bảng orders và order_details để tính tổng.
     *
     * @return danh sách Object[] gồm [customerId, firstName, lastName, phoneNumber, totalAmount]
     */
    @Query("SELECT c.id, c.firstName, c.lastName, c.phoneNumber, " +
           "COALESCE(SUM(od.priceEach * od.quantityOrder), 0) AS totalAmount " +
           "FROM Customer c LEFT JOIN Order o ON o.customer.id = c.id " +
           "LEFT JOIN OrderDetail od ON od.order.id = o.id " +
           "WHERE (o.status IS NULL OR o.status != 'CANCELLED') " +
           "GROUP BY c.id, c.firstName, c.lastName, c.phoneNumber " +
           "ORDER BY totalAmount DESC")
    java.util.List<Object[]> findCustomersWithTotalAmount();

    /**
     * Đếm số đơn hàng của mỗi khách hàng (dùng cho báo cáo).
     *
     * @return danh sách Object[] gồm [customerId, firstName, lastName, phoneNumber, orderCount]
     */
    @Query("SELECT c.id, c.firstName, c.lastName, c.phoneNumber, COUNT(o.id) AS orderCount " +
           "FROM Customer c LEFT JOIN Order o ON o.customer.id = c.id " +
           "WHERE (o.status IS NULL OR o.status != 'CANCELLED') " +
           "GROUP BY c.id, c.firstName, c.lastName, c.phoneNumber " +
           "ORDER BY orderCount DESC")
    java.util.List<Object[]> findCustomersWithOrderCount();

}
