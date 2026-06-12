/**
 * checkout.js — Xử lý quy trình đặt hàng / thanh toán.
 * Lấy giỏ hàng từ localStorage, hiển thị review đơn hàng,
 * và gửi POST /api/orders khi xác nhận.
 * Phụ thuộc: api.js, cart.js
 */

// ===== Khi trang checkout.html load =====
document.addEventListener('DOMContentLoaded', () => {
    renderCheckoutSummary();
});

/**
 * Hiển thị tóm tắt đơn hàng (sản phẩm + tổng tiền) trên trang checkout.
 */
function renderCheckoutSummary() {
    const cart = getCart();
    const summaryContainer = document.getElementById('orderSummaryList');
    const totalEl = document.getElementById('checkoutTotal');

    if (!summaryContainer) return;

    if (cart.length === 0) {
        // Giỏ trống → quay lại trang sản phẩm
        summaryContainer.innerHTML = '<p class="text-muted">Giỏ hàng trống. <a href="/products.html">Tiếp tục mua sắm</a></p>';
        return;
    }

    let html = '';
    cart.forEach(item => {
        html += `
            <div class="d-flex justify-content-between mb-2">
                <span>${item.productName} × ${item.quantity}</span>
                <span class="fw-bold">${formatCurrency(item.buyPrice * item.quantity)}</span>
            </div>`;
    });

    summaryContainer.innerHTML = html;

    // Hiển thị tổng tiền
    if (totalEl) totalEl.textContent = formatCurrency(getCartTotalAmount());
}

/**
 * Xác nhận đơn hàng — gửi POST /api/orders.
 * Logic SĐT:
 * - Backend kiểm tra SĐT trong DB
 * - Trùng → cập nhật KH cũ + thêm order mới
 * - Mới → tạo KH mới + order mới
 */
async function confirmOrder() {
    const cart = getCart();
    if (cart.length === 0) {
        showAlert('warning', 'Giỏ hàng trống!');
        return;
    }

    // Lấy thông tin người mua từ form
    const firstName = document.getElementById('inputFirstName').value.trim();
    const lastName  = document.getElementById('inputLastName').value.trim();
    const phone     = document.getElementById('inputPhone').value.trim();
    const address   = document.getElementById('inputAddress').value.trim();
    const city      = document.getElementById('inputCity').value.trim();
    const country   = document.getElementById('inputCountry').value.trim() || 'Việt Nam';
    const comments  = document.getElementById('inputComments')?.value.trim() || '';

    // Validate thông tin bắt buộc
    if (!firstName || !lastName || !phone || !address || !city) {
        showAlert('warning', 'Vui lòng nhập đầy đủ thông tin giao hàng');
        return;
    }

    // Validate SĐT (10-11 số)
    if (!/^[0-9]{10,11}$/.test(phone)) {
        showAlert('error', 'Số điện thoại không hợp lệ (cần 10-11 chữ số)');
        return;
    }

    // Chuẩn bị danh sách sản phẩm trong đơn hàng, bỏ qua các item lỗi
    const orderDetails = cart.filter(item => item.productId).map(item => ({
        productId:     item.productId,
        quantityOrder: item.quantity,
        priceEach:     item.buyPrice
    }));

    if (orderDetails.length === 0) {
        showAlert('error', 'Giỏ hàng của bạn đang chứa dữ liệu cũ bị lỗi. Vui lòng xóa giỏ hàng và thêm lại sản phẩm.');
        clearCart();
        return;
    }

    // DTO gửi lên backend
    const orderDto = {
        customerFirstName: firstName,
        customerLastName:  lastName,
        customerPhone:     phone,
        customerAddress:   address,
        customerCity:      city,
        customerCountry:   country,
        comments:          comments,
        orderDetails:      orderDetails
    };

    // Hiển thị loading
    const btnConfirm = document.getElementById('btnConfirmOrder');
    btnConfirm.disabled = true;
    btnConfirm.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang xử lý...';

    try {
        // Gửi đơn hàng lên backend (KHÔNG cần đăng nhập)
        const savedOrder = await apiRequest('/orders', 'POST', orderDto, false);

        // Xóa giỏ hàng sau khi đặt thành công
        clearCart();

        // Lưu orderId vào sessionStorage để trang success đọc
        sessionStorage.setItem('lastOrderId', savedOrder.id);

        // Redirect trang thành công
        window.location.href = '/order-success.html';

    } catch (error) {
        showAlert('error', 'Đặt hàng thất bại', error.message);
        btnConfirm.disabled = false;
        btnConfirm.innerHTML = '<i class="fas fa-check me-2"></i>Xác nhận đặt hàng';
    }
}
