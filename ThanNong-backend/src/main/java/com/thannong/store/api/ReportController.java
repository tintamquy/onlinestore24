package com.thannong.store.api;

import com.thannong.store.repository.CustomerRepository;
import com.thannong.store.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Controller báo cáo thống kê (chỉ ADMIN).
 * - GET /api/reports/revenue: doanh thu theo ngày (cho Chart.js line chart)
 * - GET /api/reports/customers: phân nhóm KH VIP (cho Chart.js bar chart)
 */
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Báo cáo doanh thu theo ngày trong khoảng thời gian.
     * Frontend dùng data này để vẽ Line Chart (Chart.js).
     *
     * @param fromDate ngày bắt đầu (format: yyyy-MM-dd)
     * @param toDate   ngày kết thúc (format: yyyy-MM-dd)
     * @return Map chứa labels (ngày) và data (doanh thu)
     */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

        List<Object[]> rawData = orderRepository.findDailyRevenue(fromDate, toDate);

        // Chuẩn bị data cho Chart.js
        List<String> labels  = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();

        for (Object[] row : rawData) {
            labels.add(String.valueOf(row[0]));             // ngày
            data.add((BigDecimal) row[1]);                  // tổng doanh thu
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data",   data);
        return ResponseEntity.ok(response);
    }

    /**
     * Báo cáo phân nhóm khách hàng theo tổng tiền mua.
     * Phân nhóm VIP:
     * - Bạch kim: > 50 triệu
     * - Vàng:     > 20 triệu
     * - Bạc:      > 10 triệu
     * - VIP:      > 5 triệu
     * - Thường:   còn lại
     *
     * Frontend dùng data này để vẽ Bar Chart (Chart.js).
     *
     * @param groupBy "amount" (theo tổng tiền) hoặc "orderCount" (theo số đơn)
     * @return danh sách khách hàng với nhóm VIP và thông tin tổng hợp
     */
    @GetMapping("/customers")
    public ResponseEntity<List<Map<String, Object>>> getCustomerReport(
            @RequestParam(defaultValue = "amount") String groupBy) {

        List<Object[]> rawData;
        List<Map<String, Object>> result = new ArrayList<>();

        if ("orderCount".equals(groupBy)) {
            // Nhóm theo số đơn hàng
            rawData = customerRepository.findCustomersWithOrderCount();
            for (Object[] row : rawData) {
                Map<String, Object> item = new HashMap<>();
                item.put("customerId",   row[0]);
                item.put("firstName",    row[1]);
                item.put("lastName",     row[2]);
                item.put("phoneNumber",  row[3]);
                item.put("orderCount",   row[4]);
                result.add(item);
            }
        } else {
            // Nhóm theo tổng tiền mua (mặc định)
            rawData = customerRepository.findCustomersWithTotalAmount();
            for (Object[] row : rawData) {
                BigDecimal totalAmount = (BigDecimal) row[4];
                String tier = classifyCustomerTier(totalAmount);

                Map<String, Object> item = new HashMap<>();
                item.put("customerId",   row[0]);
                item.put("firstName",    row[1]);
                item.put("lastName",     row[2]);
                item.put("phoneNumber",  row[3]);
                item.put("totalAmount",  totalAmount);
                item.put("tier",         tier); // Bạch Kim / Vàng / Bạc / VIP / Thường
                result.add(item);
            }
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Phân loại khách hàng vào nhóm VIP dựa trên tổng tiền đã mua.
     *
     * @param totalAmount tổng tiền đã mua (VNĐ)
     * @return tên nhóm: "Bạch Kim", "Vàng", "Bạc", "VIP", "Thường"
     */
    private String classifyCustomerTier(BigDecimal totalAmount) {
        if (totalAmount == null) return "Thường";
        long amount = totalAmount.longValue();

        if (amount > 50_000_000L) return "Bạch Kim";
        if (amount > 20_000_000L) return "Vàng";
        if (amount > 10_000_000L) return "Bạc";
        if (amount >  5_000_000L) return "VIP";
        return "Thường";
    }

}
