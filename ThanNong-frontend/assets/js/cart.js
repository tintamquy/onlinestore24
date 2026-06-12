/**
 * cart.js — Quản lý giỏ hàng bằng localStorage.
 * Phụ thuộc: api.js
 *
 * Cấu trúc giỏ hàng lưu trong localStorage (key: 'thanNong_cart'):
 * [
 *   { productId, productName, imageUrl, buyPrice, quantity },
 *   ...
 * ]
 */

/** Key lưu giỏ hàng trong localStorage */
const CART_KEY = 'thanNong_cart';

// ===== Đọc / ghi giỏ hàng =====

/**
 * Lấy danh sách sản phẩm trong giỏ hàng từ localStorage.
 * @returns {Array} mảng cart item
 */
function getCart() {
    const cartJson = localStorage.getItem(CART_KEY);
    return cartJson ? JSON.parse(cartJson) : [];
}

/**
 * Lưu giỏ hàng vào localStorage.
 * @param {Array} cart mảng cart item
 */
function saveCart(cart) {
    localStorage.setItem(CART_KEY, JSON.stringify(cart));
    updateCartBadge(); // Cập nhật số lượng trên icon giỏ hàng
}

// ===== Thêm / xóa sản phẩm =====

/**
 * Thêm sản phẩm vào giỏ hàng.
 * Nếu SP đã có → tăng số lượng thêm.
 * Nếu chưa có → thêm mới vào mảng.
 *
 * @param {Object} product  object sản phẩm {id, productName, imageUrl, buyPrice}
 * @param {number} quantity số lượng muốn thêm (mặc định 1)
 */
function addToCart(product, quantity = 1) {
    const cart = getCart();

    // Kiểm tra SP đã có trong giỏ chưa
    const existingIndex = cart.findIndex(item => item.productId === product.id);

    if (existingIndex >= 0) {
        // Đã có → tăng số lượng
        cart[existingIndex].quantity += quantity;
    } else {
        // Chưa có → thêm mới
        cart.push({
            productId:   product.id,
            productName: product.productName,
            imageUrl:    product.imageUrl,
            buyPrice:    product.buyPrice,
            quantity:    quantity
        });
    }

    saveCart(cart);
    showAlert('success', 'Đã thêm vào giỏ hàng!', product.productName);
}

/**
 * Tăng số lượng sản phẩm trong giỏ hàng.
 * @param {number} productId ID sản phẩm
 */
function increaseQuantity(productId) {
    const cart = getCart();
    const item = cart.find(i => i.productId === productId);
    if (item) {
        item.quantity += 1;
        saveCart(cart);
    }
}

/**
 * Giảm số lượng sản phẩm trong giỏ hàng.
 * Nếu số lượng về 0 → xóa khỏi giỏ.
 * @param {number} productId ID sản phẩm
 */
function decreaseQuantity(productId) {
    const cart = getCart();
    const itemIndex = cart.findIndex(i => i.productId === productId);
    if (itemIndex >= 0) {
        cart[itemIndex].quantity -= 1;
        if (cart[itemIndex].quantity <= 0) {
            cart.splice(itemIndex, 1); // Xóa khỏi giỏ
        }
        saveCart(cart);
    }
}

/**
 * Xóa hoàn toàn 1 sản phẩm khỏi giỏ hàng.
 * @param {number} productId ID sản phẩm
 */
function removeFromCart(productId) {
    const cart = getCart().filter(i => i.productId !== productId);
    saveCart(cart);
}

/**
 * Xóa toàn bộ giỏ hàng.
 */
function clearCart() {
    localStorage.removeItem(CART_KEY);
    updateCartBadge();
}

// ===== Tính toán =====

/**
 * Tính tổng số lượng sản phẩm trong giỏ hàng.
 * @returns {number} tổng số lượng
 */
function getCartTotalQuantity() {
    return getCart().reduce((sum, item) => sum + item.quantity, 0);
}

/**
 * Tính tổng tiền giỏ hàng.
 * @returns {number} tổng tiền (VNĐ)
 */
function getCartTotalAmount() {
    return getCart().reduce((sum, item) => sum + (item.buyPrice * item.quantity), 0);
}

// ===== Cập nhật UI =====

/**
 * Cập nhật badge số lượng trên icon giỏ hàng ở navbar.
 * Tìm element có id="cartBadge" và cập nhật số.
 */
function updateCartBadge() {
    const badgeEl = document.getElementById('cartBadge');
    if (badgeEl) {
        const total = getCartTotalQuantity();
        badgeEl.textContent = total;
        badgeEl.style.display = total > 0 ? 'inline-block' : 'none';
    }
}

/**
 * Render toàn bộ giỏ hàng vào trang cart.html.
 * Tìm element id="cartContainer" để render vào.
 */
function renderCart() {
    const cartContainer = document.getElementById('cartContainer');
    const cartSummary   = document.getElementById('cartSummary');
    const cart          = getCart();

    if (!cartContainer) return;

    // Giỏ hàng trống
    if (cart.length === 0) {
        cartContainer.innerHTML = `
            <div class="empty-cart text-center py-5">
                <i class="fas fa-shopping-cart fa-4x text-muted mb-3"></i>
                <h4 class="text-muted">Giỏ hàng của bạn đang trống</h4>
                <a href="/products.html" class="btn btn-primary mt-3">
                    <i class="fas fa-shopping-bag me-2"></i>Tiếp tục mua sắm
                </a>
            </div>`;
        if (cartSummary) cartSummary.style.display = 'none';
        return;
    }

    // Render danh sách SP trong giỏ
    let htmlItems = '';
    cart.forEach(item => {
        const subTotal = item.buyPrice * item.quantity;
        htmlItems += `
            <div class="cart-item" id="cartItem-${item.productId}">
                <div class="row align-items-center py-3 border-bottom">
                    <div class="col-md-2">
                        <img src="${getProductImageUrl(item.imageUrl)}"
                             alt="${item.productName}"
                             class="img-fluid rounded"
                             style="max-height:80px; object-fit:cover"
                             onerror="this.src='/assets/images/products/placeholder.jpg'">
                    </div>
                    <div class="col-md-4">
                        <h6 class="mb-0">${item.productName}</h6>
                        <small class="text-muted">${formatCurrency(item.buyPrice)}/đơn vị</small>
                    </div>
                    <div class="col-md-3">
                        <div class="input-group input-group-sm quantity-control">
                            <button class="btn btn-outline-secondary"
                                    onclick="changeQuantity(${item.productId}, -1)">−</button>
                            <span class="input-group-text" id="qty-${item.productId}">${item.quantity}</span>
                            <button class="btn btn-outline-secondary"
                                    onclick="changeQuantity(${item.productId}, 1)">+</button>
                        </div>
                    </div>
                    <div class="col-md-2 text-end fw-bold" id="subtotal-${item.productId}">
                        ${formatCurrency(subTotal)}
                    </div>
                    <div class="col-md-1 text-end">
                        <button class="btn btn-sm btn-outline-danger"
                                onclick="deleteCartItem(${item.productId})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>`;
    });

    cartContainer.innerHTML = htmlItems;

    // Cập nhật tổng tiền
    if (cartSummary) {
        cartSummary.style.display = 'block';
        const totalEl = document.getElementById('cartTotalAmount');
        if (totalEl) totalEl.textContent = formatCurrency(getCartTotalAmount());
    }
}

/**
 * Tăng hoặc giảm số lượng từ trang giỏ hàng, sau đó re-render.
 * @param {number} productId ID sản phẩm
 * @param {number} delta     +1 hoặc -1
 */
function changeQuantity(productId, delta) {
    if (delta > 0) {
        increaseQuantity(productId);
    } else {
        decreaseQuantity(productId);
    }
    renderCart(); // Re-render sau khi thay đổi
}

/**
 * Xóa 1 sản phẩm khỏi giỏ hàng và re-render.
 * @param {number} productId ID sản phẩm
 */
function deleteCartItem(productId) {
    removeFromCart(productId);
    renderCart();
}

// Gọi updateCartBadge và renderCart khi DOM load xong
document.addEventListener('DOMContentLoaded', () => {
    updateCartBadge();
    renderCart(); // Chỉ có hiệu lực trên trang cart.html (kiểm tra element tồn tại)
});
