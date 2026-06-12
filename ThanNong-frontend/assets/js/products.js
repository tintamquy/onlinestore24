/**
 * products.js — Logic trang danh mục sản phẩm.
 * Phụ thuộc: api.js, cart.js
 */

/** Trang hiện tại (0-indexed) */
let currentPage = 0;
const PAGE_SIZE  = 12;

// ===== Khởi tạo =====
document.addEventListener('DOMContentLoaded', async () => {
    await loadCategories();     // Load dropdown danh mục
    await searchProducts();     // Load sản phẩm lần đầu

    // Đọc query param nếu có (ví dụ: products.html?category=2)
    const urlParams = new URLSearchParams(window.location.search);
    const catParam = urlParams.get('category');
    if (catParam) {
        document.getElementById('filterCategory').value = catParam;
        await searchProducts();
    }
});

/**
 * Load danh sách danh mục vào dropdown.
 */
async function loadCategories() {
    try {
        const categories = await apiRequest('/product-lines', 'GET', null, false);
        const selectEl = document.getElementById('filterCategory');
        categories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = cat.productLine;
            selectEl.appendChild(option);
        });
    } catch (err) {
        console.error('Lỗi tải danh mục:', err);
    }
}

/**
 * Tìm kiếm và lọc sản phẩm, render vào grid.
 * Được gọi khi thay đổi bất kỳ filter nào.
 *
 * @param {number} page trang muốn load (mặc định 0)
 */
async function searchProducts(page = 0) {
    currentPage = page;

    // Đọc giá trị các filter
    const keyword        = document.getElementById('searchKeyword').value.trim();
    const categoryId     = document.getElementById('filterCategory').value;
    const minPrice       = document.getElementById('filterMinPrice').value;
    const maxPrice       = document.getElementById('filterMaxPrice').value;
    const sortBy         = document.getElementById('filterSort').value;

    // Xây dựng query string
    let queryParams = `?page=${page}&size=${PAGE_SIZE}&sortBy=${sortBy}`;
    if (keyword)    queryParams += `&keyword=${encodeURIComponent(keyword)}`;
    if (categoryId) queryParams += `&productLineId=${categoryId}`;
    if (minPrice)   queryParams += `&minPrice=${minPrice}`;
    if (maxPrice)   queryParams += `&maxPrice=${maxPrice}`;

    // Hiển thị loading
    const grid = document.getElementById('productGrid');
    grid.innerHTML = `<div class="col-12 text-center py-5">
        <div class="spinner-border" style="color:var(--color-primary)"></div>
        <p class="mt-2 text-muted">Đang tìm kiếm...</p>
    </div>`;

    try {
        const response = await apiRequest(`/products${queryParams}`, 'GET', null, false);
        renderProducts(response.data);
        renderPagination(response.currentPage, response.totalPages);

        // Cập nhật thông tin kết quả
        document.getElementById('resultInfo').textContent =
            `Tìm thấy ${response.totalItems} sản phẩm`;

    } catch (err) {
        grid.innerHTML = `<div class="col-12 text-center text-danger py-4">
            <i class="fas fa-exclamation-circle me-2"></i>
            Lỗi tải sản phẩm: ${err.message}
        </div>`;
    }
}

/**
 * Render mảng sản phẩm vào #productGrid.
 * @param {Array} products mảng ProductDto
 */
function renderProducts(products) {
    const grid = document.getElementById('productGrid');

    if (!products || products.length === 0) {
        grid.innerHTML = `<div class="col-12 text-center py-5 text-muted">
            <i class="fas fa-search fa-3x mb-3"></i>
            <h5>Không tìm thấy sản phẩm nào</h5>
            <p>Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
        </div>`;
        return;
    }

    grid.innerHTML = products.map((product, idx) => `
        <div class="col-6 col-md-4 fade-in-up" style="animation-delay:${idx * .06}s">
            <div class="product-card">
                <div class="product-card-img-wrap">
                    <a href="product-detail.html?id=${product.id}">
                        <img src="${getProductImageUrl(product.imageUrl)}"
                             alt="${product.productName}"
                             loading="lazy"
                             onerror="this.src='/assets/images/products/placeholder.jpg'">
                    </a>
                </div>
                <div class="product-card-body">
                    <div class="product-card-category">${product.productLineName || 'Thảo dược'}</div>
                    <h3 class="product-card-title">
                        <a href="product-detail.html?id=${product.id}" class="text-dark text-decoration-none">
                            ${product.productName}
                        </a>
                    </h3>
                    <div class="product-rating">
                        ${'★'.repeat(Math.round(product.averageRating || 5))}
                        ${'☆'.repeat(5 - Math.round(product.averageRating || 5))}
                        <span class="text-muted small">(${product.reviewCount || 0})</span>
                    </div>
                    <div class="product-card-price">${formatCurrency(product.buyPrice)}</div>
                    <button class="btn-add-to-cart"
                            onclick="addToCart({
                                id:${product.id},
                                productName:'${product.productName.replace(/'/g,"\\'")}',
                                imageUrl:'${product.imageUrl || ''}',
                                buyPrice:${product.buyPrice}
                            })">
                        <i class="fas fa-cart-plus me-1"></i>Thêm vào giỏ
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

/**
 * Render phân trang.
 * @param {number} currentPageNum trang hiện tại (0-indexed)
 * @param {number} totalPages     tổng số trang
 */
function renderPagination(currentPageNum, totalPages) {
    const paginationEl = document.getElementById('pagination');
    if (totalPages <= 1) {
        paginationEl.innerHTML = '';
        return;
    }

    let html = '';

    // Nút Previous
    html += `<li class="page-item ${currentPageNum === 0 ? 'disabled' : ''}">
        <button class="page-link" onclick="searchProducts(${currentPageNum - 1})">
            <i class="fas fa-chevron-left"></i>
        </button>
    </li>`;

    // Các trang
    for (let i = 0; i < totalPages; i++) {
        if (totalPages > 7 && Math.abs(i - currentPageNum) > 2 && i !== 0 && i !== totalPages - 1) {
            if (i === 1 || i === totalPages - 2) {
                html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
            continue;
        }
        html += `<li class="page-item ${i === currentPageNum ? 'active' : ''}">
            <button class="page-link" onclick="searchProducts(${i})">${i + 1}</button>
        </li>`;
    }

    // Nút Next
    html += `<li class="page-item ${currentPageNum >= totalPages - 1 ? 'disabled' : ''}">
        <button class="page-link" onclick="searchProducts(${currentPageNum + 1})">
            <i class="fas fa-chevron-right"></i>
        </button>
    </li>`;

    paginationEl.innerHTML = html;
}

/**
 * Xóa toàn bộ bộ lọc về mặc định.
 */
function resetFilters() {
    document.getElementById('searchKeyword').value  = '';
    document.getElementById('filterCategory').value = '';
    document.getElementById('filterMinPrice').value  = '';
    document.getElementById('filterMaxPrice').value  = '';
    document.getElementById('filterSort').value      = 'id';
    searchProducts(0);
}

/**
 * Gọi searchProducts khi người dùng gõ (debounce 500ms).
 */
let searchTimeout;
function onFilterChange() {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => searchProducts(0), 500);
}
