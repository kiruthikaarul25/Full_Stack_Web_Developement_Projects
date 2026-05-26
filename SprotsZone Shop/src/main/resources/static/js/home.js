// ── Tab switch ──────────────────────────────────────
function switchTab(tab) {
  ['login','register'].forEach(t => {
    document.getElementById('tab-'+t).classList.toggle('active', t===tab);
    document.getElementById(t).classList.toggle('active', t===tab);
  });
}

// ── Password toggle ─────────────────────────────────
function togglePass(id, btn) {
  const inp = document.getElementById(id);
  inp.type = inp.type==='password' ? 'text' : 'password';
  btn.textContent = inp.type==='password' ? '👁️' : '🙈';
}

// ── Toast ───────────────────────────────────────────
function showToast(msg, type='success') {
  const t = document.getElementById('toast');
  t.textContent = (type==='success'?'✅ ':'❌ ') + msg;
  t.className = 'toast '+type+' show';
  setTimeout(()=>t.classList.remove('show'), 3500);
}

// ── REGISTER - Backend API call ────────────────────
function handleRegister() {
  const first = document.getElementById('regFirst').value.trim();
  const last  = document.getElementById('regLast').value.trim();
  const email = document.getElementById('regEmail').value.trim().toLowerCase();
  const phone = document.getElementById('regPhone').value.trim();
  const pass  = document.getElementById('regPass').value;

  if (!first || !last)             return showToast('பெயர் enter பண்ணுங்க', 'error');
  if (!email||!email.includes('@')) return showToast('Valid email enter பண்ணுங்க', 'error');
  if (!phone||phone.length<10)     return showToast('Valid phone number enter பண்ணுங்க', 'error');
  if (!pass||pass.length<6)        return showToast('Password 6+ characters வேணும்', 'error');

  const formData = new URLSearchParams();
  formData.append('firstName', first);
  formData.append('lastName', last);
  formData.append('email', email);
  formData.append('phone', phone);
  formData.append('password', pass);

  fetch('/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: formData.toString(),
    redirect: 'follow'
  }).then(res => {
    if (res.url && res.url.includes('/main')) {
      window.location.href = '/main';
    } else {
      return res.text();
    }
  }).then(html => {
    if (html && html.includes('already registered')) {
      showToast('இந்த Email already registered!', 'error');
    } else if (html) {
      // redirected to main
      window.location.href = '/main';
    }
  }).catch(() => {
    // On redirect, fetch throws - go to main
    window.location.href = '/main';
  });
}

// ── LOGIN - Backend API call ──────────────────────
function handleLogin() {
  const email = document.getElementById('loginEmail').value.trim().toLowerCase();
  const pass  = document.getElementById('loginPass').value;

  if (!email) return showToast('Email enter பண்ணுங்க', 'error');
  if (!pass)  return showToast('Password enter பண்ணுங்க', 'error');

  const formData = new URLSearchParams();
  formData.append('email', email);
  formData.append('password', pass);

  fetch('/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: formData.toString(),
    redirect: 'manual'
  }).then(res => {
    if (res.type === 'opaqueredirect' || res.status === 302 || res.redirected) {
      window.location.href = '/main';
    } else {
      return res.text().then(html => {
        if (html.includes('Invalid') || html.includes('loginError')) {
          showToast('Invalid email or password!', 'error');
        } else {
          window.location.href = '/main';
        }
      });
    }
  }).catch(() => {
    window.location.href = '/main';
  });
}

// ── Show active tab on load (for error returns) ───
document.addEventListener('DOMContentLoaded', () => {
  const urlParams = new URLSearchParams(window.location.search);
  const tab = urlParams.get('tab');
  if (tab) switchTab(tab);
});

// ── Logout (login page dashboard section) ──────────
function logout() {
  window.location.href = '/logout';
}

// ── DB Modal close ──────────────────────────────────
function closeDB() {
  document.getElementById('dbModal').style.display = 'none';
}
