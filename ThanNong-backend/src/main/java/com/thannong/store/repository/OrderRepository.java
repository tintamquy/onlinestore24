package com.thannong.store.repository;

import com.thannong.store.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository truy vấn bảng orders.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Lấy tất cả đơn hàng của một khách hàng.
     * @param customerId ID khách hàng
     * @return danh sách đơn hàng
     */
    List<Order> findByCustomerId(Integer customerId);

    /**
     * Lấy tất cả đơn hàng của một khách hàng dựa vào số điện thoại.
     * @param phoneNumber Số điện thoại khách hàng
     * @return danh sách đơn hàng
     */
    List<Order> findByCustomer_PhoneNumber(String phoneNumber);

    /**
     * Lấy doanh thu theo ngày trong khoảng thời gian.
     * Dùng cho biểu đồ doanh thu hàng ngày.
     *
     * @param fromDate ngày bắt đầu
     * @param toDate   ngày kết thúc
     * @return danh sách Object[] gồm [ngày, tổng doanh thu]
     */
    @Query("SELECT DATE(o.orderDate), SUM(od.priceEach * od.quantityOrder) " +
           "FROM Order o JOIN OrderDetail od ON od.order.id = o.id " +
           "WHERE o.status != 'CANCELLED' " +
           "AND o.orderDate BETWEEN :fromDate AND :toDate " +
           "GROUP BY DATE(o.orderDate) ORDER BY DATE(o.orderDate)")
    List<Object[]> findDailyRevenue(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

}
