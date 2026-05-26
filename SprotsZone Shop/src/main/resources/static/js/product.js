const _data = document.getElementById('thData').dataset;
const productId = parseInt(_data.productId) || 0;
let qty = 1;

function changeQty(d) {
  qty = Math.max(1, Math.min(10, qty + d));
  document.getElementById('qtyVal').textContent = qty;
}

function showToast(icon, msg) {
  const wrap = document.getElementById('toastWrap');
  const el = document.createElement('div');
  el.className = 'toast-msg';
  el.innerHTML = `<span class="t-icon">${icon}</span><span>${msg}</span>`;
  wrap.appendChild(el);
  setTimeout(() => el.classList.add('show'), 10);
  setTimeout(() => {
    el.classList.remove('show');
    setTimeout(() => el.remove(), 350);
  }, 3000);
}

function addToCartPD(id) {
  fetch('/cart/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: `productId=${id}&qty=${qty}`
  })
    .then(r => r.text())
    .then(res => {
      if (res === 'LOGIN_REQUIRED') window.location.href = '/login';
      else showToast('🛒', 'Added to cart!');
    })
    .catch(() => showToast('⚠️', 'Something went wrong. Try again.'));
}

function buyNow(id) {
  fetch('/cart/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: `productId=${id}&qty=${qty}`
  })
    .then(r => r.text())
    .then(res => {
      if (res === 'LOGIN_REQUIRED') window.location.href = '/login';
      else window.location.href = '/checkout';
    })
    .catch(() => showToast('⚠️', 'Something went wrong. Try again.'));
}

function setImg(src, el) {
  document.getElementById('mainImg').src = src;
  document.querySelectorAll('.thumb').forEach(t => t.classList.remove('active'));
  el.classList.add('active');
}

function toggleWish(id) {
  const btn = document.getElementById('wishBtn');
  const isWished = btn.dataset.wished === 'true';

  fetch('/wishlist/toggle', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: `productId=${id}`
  })
    .then(r => r.text())
    .then(res => {
      if (res === 'LOGIN_REQUIRED') {
        window.location.href = '/login';
        return;
      }
      const nowWished = !isWished;
      btn.dataset.wished = String(nowWished);
      btn.textContent = nowWished ? '❤️' : '🤍';
      showToast(nowWished ? '❤️' : '🤍', nowWished ? 'Added to wishlist!' : 'Removed from wishlist');
    })
    .catch(() => showToast('⚠️', 'Something went wrong. Try again.'));
}

function renderStars(rating) {
  const el = document.querySelector('.pd-stars');
  if (!el) return;
  const safe = typeof rating === 'number' && !isNaN(rating)
    ? Math.min(5, Math.max(0, rating)) : 0;
  const full = Math.floor(safe);
  const half = safe % 1 >= 0.5;
  const empty = 5 - full - (half ? 1 : 0);
  el.textContent = '★'.repeat(full) + (half ? '½' : '') + '☆'.repeat(empty);
}

// Runs once DOM is ready — reads rating from data attribute
renderStars(parseFloat(_data.rating) || 0);