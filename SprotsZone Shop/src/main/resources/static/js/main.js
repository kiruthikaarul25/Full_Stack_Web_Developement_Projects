// ── Sidebar Toggle ──
let sidebarOpen = true;
function toggleSidebar() {
  const sb = document.getElementById('sidebar');
  const main = document.getElementById('mainContent');
  if (!sb || !main) return;
  sidebarOpen = !sidebarOpen;
  if (sidebarOpen) {
    sb.style.width = '';
    sb.style.minWidth = '';
    sb.style.overflow = '';
    sb.style.padding = '';
    main.style.marginLeft = '';
  } else {
    sb.style.width = '0';
    sb.style.minWidth = '0';
    sb.style.overflow = 'hidden';
    sb.style.padding = '0';
    main.style.marginLeft = '0';
  }
}

// ── Hero Carousel ──
let cur = 0;
const slides = document.querySelectorAll('.slide');
const dots = document.querySelectorAll('.dot');
function goSlide(n) {
  if (!slides.length) return;
  slides[cur].classList.remove('active');
  dots[cur].classList.remove('active');
  cur = (n + slides.length) % slides.length;
  slides[cur].classList.add('active');
  dots[cur].classList.add('active');
}
function moveSlide(d) { goSlide(cur + d); }
if (slides.length > 0) setInterval(() => moveSlide(1), 5000);

// ── Flash Sale Timer ──
let secs = 2 * 3600 + 47 * 60 + 30;
function tick() {
  secs--;
  if (secs < 0) secs = 3600;
  const h = Math.floor(secs / 3600), m = Math.floor((secs % 3600) / 60), s = secs % 60;
  const th = document.getElementById('th');
  const tm = document.getElementById('tm');
  const ts = document.getElementById('ts');
  if (th) th.textContent = String(h).padStart(2, '0');
  if (tm) tm.textContent = String(m).padStart(2, '0');
  if (ts) ts.textContent = String(s).padStart(2, '0');
}
setInterval(tick, 1000);

// ── Toast ──
function showToast(icon, msg) {
  const wrap = document.getElementById('toastWrap');
  if (!wrap) return;
  const el = document.createElement('div');
  el.className = 'toast-msg';
  el.innerHTML = `<span class="t-icon">${icon}</span><span>${msg}</span>`;
  wrap.appendChild(el);
  setTimeout(() => el.classList.add('show'), 10);
  setTimeout(() => { el.classList.remove('show'); setTimeout(() => el.remove(), 350); }, 3000);
}

// ── Cart ──
function updateCartBadge() {
  fetch('/cart/items')
    .then(r => r.json())
    .then(data => {
      if (data.error) return;
      const total = data.items.reduce((s, i) => s + i.quantity, 0);
      document.querySelectorAll('#cartCount').forEach(el => el.textContent = total);
    })
    .catch(() => {});
}

function addToCart(id) {
  fetch('/cart/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: `productId=${id}&qty=1`
  }).then(r => r.text()).then(res => {
    if (res === 'LOGIN_REQUIRED') {
      window.location.href = '/login';
    } else if (res === 'PRODUCT_NOT_FOUND') {
      showToast('❌', 'Product not found!');
    } else {
      showToast('🛒', 'Added to cart!');
      updateCartBadge();
    }
  }).catch(() => showToast('❌', 'Cart error!'));
}

// ── Wishlist ──
let wishlist = JSON.parse(localStorage.getItem('wishlist') || '[]');
function toggleWish(id) {
  const btn = document.getElementById('w' + id);
  if (wishlist.includes(id)) {
    wishlist = wishlist.filter(wid => wid !== id);
    if (btn) { btn.textContent = '🤍'; btn.classList.remove('loved'); }
    showToast('🤍', 'Removed from wishlist');
  } else {
    wishlist.push(id);
    if (btn) { btn.textContent = '❤️'; btn.classList.add('loved'); }
    showToast('❤️', 'Added to wishlist!');
  }
  localStorage.setItem('wishlist', JSON.stringify(wishlist));
}

// ── Stars ──
function starsHTML(r) {
  const full = Math.floor(r), half = r % 1 >= 0.5;
  return '★'.repeat(full) + (half ? '½' : '') + '☆'.repeat(5 - full - (half ? 1 : 0));
}

// ── Render Products ──
function renderProducts(arr, gridId) {
  const grid = document.getElementById(gridId);
  if (!grid) return;
  if (!arr.length) {
    grid.innerHTML = '<p style="color:var(--text2);padding:20px">No products found.</p>';
    return;
  }
  grid.innerHTML = arr.map(p => `
  <div class="prod-card" data-cat="${(p.cat || p.category || '').toLowerCase()}">
    <div class="prod-img">
      <img src="${p.img || p.imageUrl || ''}" alt="${p.name}" loading="lazy"
           onerror="this.style.display='none';this.parentNode.style.background='var(--surface2)'"/>
      <div class="prod-overlay">
        <button class="quick-btn" onclick="window.location.href='/product/${p.id}'">👁 Quick View</button>
      </div>
    </div>
    <span class="pbadge ${p.badge || 'new'}">${(p.badge || 'new').toUpperCase()}</span>
    <button class="wish-btn ${wishlist.includes(p.id) ? 'loved' : ''}" id="w${p.id}" onclick="toggleWish(${p.id})">
      ${wishlist.includes(p.id) ? '❤️' : '🤍'}
    </button>
    <div class="prod-info">
      <div class="prod-brand">${p.brand || ''}</div>
      <div class="prod-name">${p.name}</div>
      <div class="prod-rating">
        <span class="stars">${starsHTML(p.rating || 4)}</span>
        <span class="r-num">${p.rating || 4}</span>
        <span class="r-count">(${(p.count || p.reviewCount || 0).toLocaleString('en-IN')})</span>
      </div>
      <div class="prod-price">
        <span class="p-now">₹${(p.price || p.currentPrice || 0).toLocaleString('en-IN')}</span>
        <span class="p-old">₹${(p.old || p.originalPrice || 0).toLocaleString('en-IN')}</span>
        <span class="p-off">-${p.off || p.discount || 0}%</span>
      </div>
    </div>
    <div class="prod-actions">
      <button class="cart-btn" onclick="addToCart(${p.id})">🛒 Add to Cart</button>
    </div>
  </div>`).join('');
}

// ── All products stored globally ──
let allProducts = [];

// ── Load products from DB API ──
fetch('/api/products')
  .then(r => r.json())
  .then(all => {
    allProducts = all;
    renderProducts(all.slice(0, 8), 'productGrid');
    renderProducts(all.slice(8), 'newGrid');
    updateCartBadge();
  })
  .catch(() => {
    const grid = document.getElementById('productGrid');
    if (grid) grid.innerHTML = '<p style="color:red;padding:20px">Products load ஆகல — Server running-ஆ check பண்ணுங்க</p>';
  });

// ── Category Filter ──
function filterCat(el, cat) {
  document.querySelectorAll('.cat-chip').forEach(c => c.classList.remove('on'));
  if (el) el.classList.add('on');
  const filtered = cat === 'all'
    ? allProducts.slice(0, 8)
    : allProducts.filter(p => (p.cat || p.category || '').toLowerCase() === cat.toLowerCase());
  renderProducts(filtered, 'productGrid');
}

// ── Live Search — uses class="search-drop" (not id) ──
function liveSearch(val) {
  // Find by class since HTML uses class="search-drop" not id
  const drop = document.querySelector('.search-drop');
  if (!drop) return;

  const query = val.trim().toLowerCase();

  if (!query) {
    drop.style.display = '';
    // Restore original category items
    drop.innerHTML = `
      <div class="sd-item"><em>🏏</em> Cricket Bats <span class="sd-tag">Trending</span></div>
      <div class="sd-item"><em>⚽</em> Football Boots</div>
      <div class="sd-item"><em>🏸</em> Badminton Rackets</div>
      <div class="sd-item"><em>🥊</em> Boxing Gloves</div>
      <div class="sd-item"><em>🏊</em> Swimming Gear</div>
      <div class="sd-item"><em>🏋️</em> Gym Equipment <span class="sd-tag">New</span></div>
      <div class="sd-item"><em>👟</em> Running Shoes</div>
    `;
    return;
  }

  const matched = allProducts.filter(p =>
    (p.name || '').toLowerCase().includes(query) ||
    (p.brand || '').toLowerCase().includes(query) ||
    (p.cat || p.category || '').toLowerCase().includes(query)
  ).slice(0, 8);

  if (!matched.length) {
    drop.innerHTML = '<div class="sd-item" style="color:var(--text2);cursor:default;">No results found</div>';
    drop.style.display = 'block';
    return;
  }

  drop.innerHTML = matched.map(p => `
    <div class="sd-item" onclick="window.location.href='/product/${p.id}'"
         style="display:flex;align-items:center;gap:10px;cursor:pointer;">
      <img src="${p.img || p.imageUrl || ''}" alt="${p.name}"
           style="width:34px;height:34px;object-fit:cover;border-radius:6px;flex-shrink:0;"
           onerror="this.style.display='none'"/>
      <div>
        <div style="font-weight:700;font-size:0.87rem;">${p.name}</div>
        <div style="font-size:0.73rem;color:var(--text2);">${p.brand || ''} · ₹${(p.price || p.currentPrice || 0).toLocaleString('en-IN')}</div>
      </div>
    </div>
  `).join('');
  drop.style.display = 'block';
}

// Close search dropdown on outside click
document.addEventListener('click', function(e) {
  const drop = document.querySelector('.search-drop');
  const input = document.getElementById('searchInput');
  if (drop && input && !input.contains(e.target) && !drop.contains(e.target)) {
    drop.style.display = '';
  }
});

// ── Reviews ──
const reviews = [
  { name: 'Arjun K.',   av: 'A', color: '#ff2d2d', stars: 5, text: 'Received my SS bat in 2 days! Quality is top notch, exactly like the photo. Will definitely buy again.',             prod: '🏏 SS TON Bat',       date: '2 days ago' },
  { name: 'Priya M.',   av: 'P', color: '#ff7b00', stars: 5, text: 'The Yonex racket is perfect for competitive play. Great balance and shuttle response. Loved the packaging!',         prod: '🏸 Yonex Racket',     date: '5 days ago' },
  { name: 'Rahul S.',   av: 'R', color: '#00d4ff', stars: 4, text: 'Nike shoes fit perfectly, very comfortable for long runs. Size chart was accurate. Fast delivery too!',              prod: '👟 Nike Pegasus',      date: '1 week ago' },
  { name: 'Kavitha D.', av: 'K', color: '#00e676', stars: 5, text: 'Amazing collection and great prices. The football boots are authentic. SportZone is now my go-to store!',            prod: '⚽ Adidas Predator',   date: '2 weeks ago' },
  { name: 'Sanjay R.',  av: 'S', color: '#ffc107', stars: 4, text: 'Good quality boxing gloves. Sturdy stitching and comfortable padding. Ideal for beginners and intermediates.',       prod: '🥊 Boxing Gloves',     date: '3 weeks ago' },
  { name: 'Divya N.',   av: 'D', color: '#e040fb', stars: 5, text: 'Ordered the Wilson tennis racket. Exceeded expectations! Feels very professional. Quick delivery, great packaging.',  prod: '🎾 Wilson Pro Staff',  date: '1 month ago' },
];

const reviewGrid = document.getElementById('reviewGrid');
if (reviewGrid) {
  reviewGrid.innerHTML = reviews.map(r => `
<div class="review-card">
  <div class="rv-head">
    <div class="rv-av" style="background:${r.color}">${r.av}</div>
    <div>
      <div class="rv-name">${r.name}</div>
      <div class="rv-date">${r.date}</div>
    </div>
  </div>
  <div class="rv-stars">${'★'.repeat(r.stars)}${'☆'.repeat(5 - r.stars)}</div>
  <div class="rv-text">${r.text}</div>
  <div class="rv-prod">
    <div class="rv-prod-img">${r.prod.split(' ')[0]}</div>
    <div class="rv-prod-name">${r.prod.substring(r.prod.indexOf(' ') + 1)}</div>
  </div>
</div>`).join('');
}

// ── Welcome Toast ──
setTimeout(() => showToast('👋', 'Welcome to SportZone!'), 800);

// ── Scroll helpers ──
function scrollToProducts() {
  const el = document.getElementById('productGrid');
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
}
function scrollToSection(id) {
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
}
function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' });
}