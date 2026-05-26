// ── All products (same as main.js) ──
const allProducts = [
  { id:1,  brand:'SS',       name:'TON Reserve Edition Cricket Bat',      img:'https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=400&q=80', price:4999,  old:7999,  off:38, badge:'hot',  cat:'cricket' },
  { id:2,  brand:'Adidas',   name:'Predator Elite Football Boots',         img:'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&q=80', price:6499,  old:9999,  off:35, badge:'sale', cat:'football' },
  { id:3,  brand:'Yonex',    name:'Astrox 88D Pro Badminton Racket',       img:'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=400&q=80', price:8999,  old:12000, off:25, badge:'top',  cat:'badminton' },
  { id:4,  brand:'Nike',     name:'Air Zoom Pegasus 41 Running Shoes',     img:'https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=400&q=80', price:9999,  old:14999, off:33, badge:'sale', cat:'running' },
  { id:5,  brand:'Spalding', name:'NBA Official Game Basketball',           img:'https://images.unsplash.com/photo-1546519638-68e109498ffc?w=400&q=80', price:2999,  old:4499,  off:33, badge:'new',  cat:'basketball' },
  { id:6,  brand:'Wilson',   name:'Pro Staff RF97 Tennis Racket',           img:'https://images.unsplash.com/photo-1622279457486-62dbd47a3b77?w=400&q=80', price:15999, old:22000, off:27, badge:'top',  cat:'tennis' },
  { id:7,  brand:'Decathlon',name:'Pro Boxing Gloves 16oz',                 img:'https://images.unsplash.com/photo-1585855822167-b37f61dc900a?w=400&q=80', price:1799,  old:2999,  off:40, badge:'sale', cat:'boxing' },
  { id:8,  brand:'Puma',     name:'Wired Run Pure Sneakers',                img:'https://images.unsplash.com/photo-1511556820780-d912e42b4980?w=400&q=80', price:3499,  old:4999,  off:30, badge:'new',  cat:'running' },
  { id:9,  brand:'Nike',     name:'Dri-FIT Yoga Pants',                     img:'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400&q=80', price:2299,  old:3499,  off:34, badge:'new',  cat:'yoga' },
  { id:10, brand:'MRF',      name:'Genius Grand Edition Bat',               img:'https://images.unsplash.com/photo-1540747913346-19212a4de6b9?w=400&q=80', price:5499,  old:7999,  off:31, badge:'new',  cat:'cricket' },
  { id:11, brand:'Reebok',   name:'CrossFit Nano X4 Shoes',                 img:'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400&q=80', price:7999,  old:11999, off:33, badge:'new',  cat:'gym' },
  { id:12, brand:'Adidas',   name:'Tiro 23 Football Training Kit',          img:'https://images.unsplash.com/photo-1577223625816-7546f13df25d?w=400&q=80', price:1999,  old:2999,  off:33, badge:'new',  cat:'football' },
];

// ── Load wishlist & cart from localStorage ──
let wishlist = JSON.parse(localStorage.getItem('wishlist') || '[]'); // array of ids
let cart     = JSON.parse(localStorage.getItem('cart')     || '[]');

// ── Cart badge ──
function updateCartBadge(){
  const total = cart.reduce((s, p) => s + p.qty, 0);
  const el = document.getElementById('cartCount');
  if(el) el.textContent = total;
}

// ── Toast ──
function showToast(icon, msg){
  const wrap = document.getElementById('toastWrap');
  const el = document.createElement('div');
  el.className = 'toast-msg';
  el.innerHTML = `<span class="t-icon">${icon}</span><span>${msg}</span>`;
  wrap.appendChild(el);
  setTimeout(() => el.classList.add('show'), 10);
  setTimeout(() => { el.classList.remove('show'); setTimeout(() => el.remove(), 350); }, 3000);
}

// ── Remove from wishlist ──
function removeWish(id){
  wishlist = wishlist.filter(wid => wid !== id);
  localStorage.setItem('wishlist', JSON.stringify(wishlist));
  renderWishlist();
  showToast('🤍', 'Removed from wishlist');
}

// ── Add to cart from wishlist ──
function addToCart(id){
  const product = allProducts.find(p => p.id === id);
  if(!product) return;
  const existing = cart.find(c => c.id === id);
  if(existing){
    existing.qty++;
  } else {
    cart.push({ id: product.id, brand: product.brand, name: product.name, img: product.img, price: product.price, old: product.old, qty: 1 });
  }
  localStorage.setItem('cart', JSON.stringify(cart));
  updateCartBadge();
  showToast('🛒', `${product.name.substring(0, 28)}… added to cart!`);
}

// ── Render ──
function renderWishlist(){
  const container = document.getElementById('wishItems');
  const likedProducts = allProducts.filter(p => wishlist.includes(p.id));

  if(likedProducts.length === 0){
    container.innerHTML = `
      <div class="wish-empty">
        <div>🤍</div>
        <div>Your wishlist is empty!</div>
        <div>Like products from the home page to save them here.</div>
        <a href="/main">Start Shopping →</a>
      </div>`;
    return;
  }

  container.innerHTML = `<div class="wish-grid">` + likedProducts.map(p => `
    <div class="wish-card">
      <div class="wc-img">
        <img src="${p.img}" alt="${p.name}" onerror="this.style.display='none'"/>
        <button class="wc-remove" onclick="removeWish(${p.id})" title="Remove">❌</button>
        <span class="wc-badge ${p.badge}">${p.badge.toUpperCase()}</span>
      </div>
      <div class="wc-info">
        <div class="wc-brand">${p.brand}</div>
        <div class="wc-name">${p.name}</div>
        <div class="wc-price">
          <span class="wc-now">₹${p.price.toLocaleString('en-IN')}</span>
          <span class="wc-old">₹${p.old.toLocaleString('en-IN')}</span>
          <span class="wc-off">-${p.off}%</span>
        </div>
      </div>
      <div class="wc-actions">
        <button class="wc-cart" onclick="addToCart(${p.id})">🛒 Add to Cart</button>
      </div>
    </div>`).join('') + `</div>`;
}

renderWishlist();
updateCartBadge();