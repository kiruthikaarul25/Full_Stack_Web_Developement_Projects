let cartData = [];

async function loadCart() {
  const res = await fetch('/cart/items');
  const data = await res.json();

  if (data.error) {
    window.location.href = '/login';
    return;
  }

  cartData = data.items;
  renderCart();
}

function renderCart() {
  const container = document.getElementById('cartItems');
  const summary   = document.getElementById('cartSummary');

  if (cartData.length === 0) {
    container.innerHTML = `
      <div class="empty-cart">
        <div class="empty-icon">🛒</div>
        <div class="empty-title">Your Cart Was Empty</div>
        <button onclick="window.location.href='/main'" class="btn-shop"> Start Shopping </button>
      </div>`;
    summary.style.display = 'none';
    return;
  }

  container.innerHTML = cartData.map(item => `
    <div class="cart-item" id="item-${item.cartItemId}">
      <img src="${item.imageUrl}" alt="${item.name}" class="item-img"/>
      <div class="item-info">
        <div class="item-name">${item.name}</div>
        <div class="item-brand">${item.brand}</div>
        <div class="item-price">₹${item.price.toLocaleString('en-IN')}</div>
      </div>
      <div class="qty-control">
        <button onclick="updateQty(${item.cartItemId}, ${item.quantity - 1})">−</button>
        <span>${item.quantity}</span>
        <button onclick="updateQty(${item.cartItemId}, ${item.quantity + 1})">+</button>
      </div>
      <div class="item-subtotal">₹${item.subtotal.toLocaleString('en-IN')}</div>
      <button class="remove-btn" onclick="removeItem(${item.cartItemId})">✕</button>
    </div>
  `).join('');

  updateSummary();
  summary.style.display = 'block';
}

function updateSummary() {
  const subtotal = cartData.reduce((s, i) => s + i.subtotal, 0);
  const discount = Math.floor(subtotal * 0.05); // 5% discount
  const total    = subtotal - discount;

  document.getElementById('subtotal').textContent  = '₹' + subtotal.toLocaleString('en-IN');
  document.getElementById('discount').textContent  = '-₹' + discount.toLocaleString('en-IN');
  document.getElementById('totalPrice').textContent = '₹' + total.toLocaleString('en-IN');
}

async function updateQty(cartItemId, qty) {
  await fetch('/cart/update', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: `cartItemId=${cartItemId}&qty=${qty}`
  });
  loadCart();
}

async function removeItem(cartItemId) {
  await fetch('/cart/remove', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: `cartItemId=${cartItemId}`
  });
  loadCart();
}

function checkout() {
  window.location.href = '/checkout';
}

// Page load-ல் cart load ஆகும்
loadCart();