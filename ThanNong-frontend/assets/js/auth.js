/**
 * auth.js — Xử lý đăng nhập, đăng ký, và quản lý phiên người dùng.
 * Phụ thuộc: api.js (phải load trước trong HTML)
 */

// ===== Hàm đăng nhập =====

/**
 * Gửi request đăng nhập, lưu token + thông tin user vào localStorage.
 * Sau đó redirect theo role:
 * - ROLE_ADMIN   → /admin/dashboard.html
 * - ROLE_CUSTOMER → /index.html
 */
async function handleLogin() {
    const username = document.getElementById('inputUsername').value.trim();
    const password = document.getElementById('inputPassword').value;

    if (!username || !password) {
        showAlert('warning', 'Vui lòng nhập đầy đủ thông tin');
        return;
    }

    // Hiển thị loading
    const btnLogin = document.getElementById('btnLogin');
    btnLogin.disabled = true;
    btnLogin.textContent = 'Đang đăng nhập...';

    try {
        const response = await apiRequest('/auth/login', 'POST',
            { username, password }, false);

        // Lưu token và thông tin user vào localStorage
        localStorage.setItem(TOKEN_KEY, response.token);
        localStorage.setItem(USER_KEY, JSON.stringify({
            username:   response.username,
            roles:      response.roles,
            customerId: response.customerId
        }));

        showAlert('success', 'Đăng nhập thành công!');

        // Redirect theo role sau 1 giây
        setTimeout(() => {
            if (response.roles.includes('ROLE_ADMIN')) {
                window.location.href = '/admin/dashboard.html';
            } else {
                window.location.href = '/index.html';
            }
        }, 1000);

    } catch (error) {
        showAlert('error', 'Đăng nhập thất bại', error.message);
        btnLogin.disabled = false;
        btnLogin.textContent = 'Đăng nhập';
    }
}

// ===== Hàm đăng ký =====

/**
 * Gửi request đăng ký tài khoản mới.
 * Kiểm tra mật khẩu xác nhận trước khi gửi.
 */
async function handleRegister() {
    const username        = document.getElementById('inputUsername').value.trim();
    const password        = document.getElementById('inputPassword').value;
    const confirmPassword = document.getElementById('inputConfirmPassword').value;

    // Validate cơ bản
    if (!username || !password || !confirmPassword) {
        showAlert('warning', 'Vui lòng nhập đầy đủ thông tin');
        return;
    }
    if (password !== confirmPassword) {
        showAlert('error', 'Mật khẩu xác nhận không khớp');
        return;
    }
    if (password.length < 6) {
        showAlert('warning', 'Mật khẩu phải ít nhất 6 ký tự');
        return;
    }

    const btnRegister = document.getElementById('btnRegister');
    btnRegister.disabled = true;
    btnRegister.textContent = 'Đang đăng ký...';

    try {
        const message = await apiRequest('/auth/register', 'POST',
            { username, password }, false);

        showAlert('success', 'Đăng ký thành công!', 'Bạn có thể đăng nhập ngay.');

        // Redirect về trang login sau 1.5 giây
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 1500);

    } catch (error) {
        showAlert('error', 'Đăng ký thất bại', error.message);
        btnRegister.disabled = false;
        btnRegister.textContent = 'Đăng ký';
    }
}

// ===== Cập nhật header khi trang load =====

/**
 * Cập nhật navigation bar theo trạng thái đăng nhập.
 * Hiển thị tên user + nút Logout nếu đã đăng nhập,
 * hiển thị nút Đăng nhập nếu chưa đăng nhập.
 */
function updateNavbar() {
    const user       = getCurrentUser();
    const navUserEl  = document.getElementById('navUserInfo');
    const navLoginEl = document.getElementById('navLoginBtn');
    const navAdminEl = document.getElementById('navAdminBtn');

    if (user && navUserEl) {
        navUserEl.textContent = `Xin chào, ${user.username}`;
        navUserEl.style.display = 'inline-block';
        if (navLoginEl) navLoginEl.style.display = 'none';
    }
    if (user && isAdmin() && navAdminEl) {
        navAdminEl.style.display = 'inline-block';
    }
}

// Gọi updateNavbar khi DOM load xong
document.addEventListener('DOMContentLoaded', updateNavbar);
