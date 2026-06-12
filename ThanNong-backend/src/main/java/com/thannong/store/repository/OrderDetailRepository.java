package com.thannong.store.repository;

import com.thannong.store.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository truy vấn bảng order_details. */
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    /**
     * Lấy tất cả chi tiết của một đơn hàng.
     * @param orderId ID đơn hàng
     * @return danh sách order_detail
     */
    List<OrderDetail> findByOrderId(Integer orderId);

}
