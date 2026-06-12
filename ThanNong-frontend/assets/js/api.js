/**
 * api.js — Cấu hình Axios và các hàm gọi API dùng chung.
 * Mọi file JS khác import BASE_URL và hàm apiRequest từ đây.
 */

// ===== Cấu hình =====
/** URL gốc của backend API — thay bằng URL production khi deploy */
const BASE_URL = 'http://localhost:8080/api';

/** Key lưu JWT token trong localStorage */
const TOKEN_KEY  = 'thanNong_token';
/** Key lưu thông tin user trong localStorage */
const USER_KEY   = 'thanNong_user';

// ===== Tiện ích lấy token =====

/**
 * Lấy JWT token từ localStorage.
 * @returns {string|null} token hoặc null nếu chưa đăng nhập
 */
function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

/**
 * Lấy thông tin user đang đăng nhập.
 * @returns {Object|null} object {username, roles, customerId} hoặc null
 */
function getCurrentUser() {
    const userJson = localStorage.getItem(USER_KEY);
    return userJson ? JSON.parse(userJson) : null;
}

/**
 * Kiểm tra user hiện tại có role ADMIN không.
 * @returns {boolean}
 */
function isAdmin() {
    const user = getCurrentUser();
    return user && user.roles && user.roles.includes('ROLE_ADMIN');
}

/**
 * Kiểm tra đã đăng nhập chưa.
 * @returns {boolean}
 */
function isLoggedIn() {
    return !!getToken();
}

// ===== Hàm gọi API cốt lõi =====

/**
 * Gọi API với xác thực JWT tự động.
 * Nếu response 401 → redirect về trang login.
 *
 * @param {string} endpoint   đường dẫn API (ví dụ: '/products?page=0')
 * @param {string} method     HTTP method: 'GET', 'POST', 'PUT', 'DELETE'
 * @param {Object} [body]     Request body (cho POST, PUT)
 * @param {boolean} [auth]    Có gửi kèm JWT không (mặc định true)
 * @returns {Promise<any>}    Dữ liệu JSON response
 */
async function apiRequest(endpoint, method = 'GET', body = null, auth = true) {
    const headers = { 'Content-Type': 'application/json' };

    // Tự động thêm Authorization header nếu đã đăng nhập
    if (auth) {
        const token = getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
    }

    const options = { method, headers };
    if (body) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(`${BASE_URL}${endpoint}`, options);

    // Token hết hạn hoặc không hợp lệ → logout
    if (response.status === 401) {
        logout();
        window.location.href = 'login.html';
        throw new Error('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại');
    }

    // Xử lý lỗi từ server
    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Lỗi HTTP ${response.status}`);
    }

    // Parse JSON hoặc trả về text
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        return response.json();
    }
    return response.text();
}

// ===== Hàm logout =====

/**
 * Đăng xuất: xóa token và thông tin user khỏi localStorage.
 */
function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
}

/**
 * Đăng xuất và redirect về trang login.
 */
function logoutAndRedirect() {
    logout();
    if (window.location.pathname.includes('/admin/')) {
        window.location.href = '../login.html';
    } else {
        window.location.href = 'login.html';
    }
}

// ===== URL ảnh sản phẩm =====

/**
 * Tạo URL đầy đủ cho ảnh sản phẩm từ tên file.
 * @param {string} imageName tên file ảnh (ví dụ: 'dau-goi.jpg')
 * @returns {string} URL đầy đủ
 */
function getProductImageUrl(imageName) {
    if (!imageName) return 'assets/images/products/placeholder.jpg';
    // Nếu đã là URL đầy đủ thì trả nguyên
    if (imageName.startsWith('http')) return imageName;
    // Nếu là UUID hoặc tên file từ backend thì lấy từ backend URL
    return `${BASE_URL}/images/products/${imageName}`;
}

/**
 * Định dạng số tiền VNĐ.
 * @param {number} amount số tiền
 * @returns {string} ví dụ: "85.000 đ"
 */
function formatCurrency(amount) {
    if (amount == null) return '0 đ';
    return new Intl.NumberFormat('vi-VN').format(amount) + ' đ';
}

/**
 * Định dạng ngày giờ tiếng Việt.
 * @param {string|Date} dateStr chuỗi ngày
 * @returns {string} ví dụ: "11/06/2026 15:30"
 */
function formatDate(dateStr) {
    if (!dateStr) return '—';
    return new Intl.DateTimeFormat('vi-VN', {
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    }).format(new Date(dateStr));
}

/**
 * Hiển thị thông báo SweetAlert2 (đã include trong HTML).
 * @param {string} type   'success' | 'error' | 'warning' | 'info'
 * @param {string} title  Tiêu đề
 * @param {string} text   Nội dung
 */
function showAlert(type, title, text = '') {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: type, title, text, timer: type === 'success' ? 2000 : undefined });
    } else {
        alert(`${title}${text ? ': ' + text : ''}`);
    }
}
