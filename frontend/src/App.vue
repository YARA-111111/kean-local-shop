<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="header-container">
        <div class="logo" @click="$router.push('/')">
          <span class="logo-icon">🍽️</span>
          <div class="logo-text">
            <h1>Kean Local Guide</h1>
            <span class="subtitle">肯恩大学交换生本地推荐</span>
          </div>
        </div>
        <nav class="main-nav">
          <router-link to="/" class="nav-item" :class="{ active: $route.path === '/' }">
            <span class="nav-icon">🏠</span>
            <span>首页</span>
          </router-link>
          <router-link to="/businesses" class="nav-item" :class="{ active: $route.path.startsWith('/businesses') }">
            <span class="nav-icon">🏪</span>
            <span>探索商家</span>
          </router-link>
        </nav>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="app-main">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="app-footer">
      <div class="footer-content">
        <p>© 2025 Kean University Exchange Student Guide</p>
        <p class="footer-links">
          <span>Powered by Yelp Data</span>
          <span class="divider">|</span>
          <span>Made with ❤️ for Kean Students</span>
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --primary-color: #e63946;
  --primary-dark: #d62839;
  --secondary-color: #457b9d;
  --accent-color: #f4a261;
  --bg-light: #f8f9fa;
  --bg-dark: #1d3557;
  --text-dark: #2b2d42;
  --text-light: #8d99ae;
  --white: #ffffff;
  --shadow-sm: 0 2px 8px rgba(0,0,0,0.08);
  --shadow-md: 0 4px 16px rgba(0,0,0,0.12);
  --shadow-lg: 0 8px 32px rgba(0,0,0,0.16);
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 20px;
}

#app {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-light);
  color: var(--text-dark);
}

/* 顶部导航栏 */
.app-header {
  background: linear-gradient(135deg, var(--bg-dark) 0%, #264653 100%);
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: var(--shadow-md);
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 72px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.logo:hover {
  transform: scale(1.02);
}

.logo-icon {
  font-size: 36px;
}

.logo-text h1 {
  font-size: 22px;
  font-weight: 700;
  color: var(--white);
  letter-spacing: -0.5px;
}

.logo-text .subtitle {
  font-size: 12px;
  color: rgba(255,255,255,0.7);
  font-weight: 400;
}

.main-nav {
  display: flex;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  color: rgba(255,255,255,0.85);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  border-radius: var(--radius-lg);
  transition: all 0.2s;
}

.nav-item:hover {
  background: rgba(255,255,255,0.1);
  color: var(--white);
}

.nav-item.active {
  background: var(--primary-color);
  color: var(--white);
}

.nav-icon {
  font-size: 18px;
}

/* 主内容区 */
.app-main {
  flex: 1;
  padding: 32px 24px;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
}

/* 页脚 */
.app-footer {
  background: var(--bg-dark);
  padding: 24px;
  margin-top: auto;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  text-align: center;
}

.footer-content p {
  color: rgba(255,255,255,0.7);
  font-size: 14px;
}

.footer-links {
  margin-top: 8px;
  font-size: 12px !important;
}

.footer-links .divider {
  margin: 0 12px;
  opacity: 0.5;
}

/* Element Plus 覆盖样式 */
.el-card {
  border-radius: var(--radius-md) !important;
  border: none !important;
  box-shadow: var(--shadow-sm) !important;
  transition: all 0.3s !important;
}

.el-card:hover {
  box-shadow: var(--shadow-md) !important;
}

.el-button--primary {
  background: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
}

.el-button--primary:hover {
  background: var(--primary-dark) !important;
  border-color: var(--primary-dark) !important;
}

.el-tag {
  border-radius: var(--radius-sm) !important;
}

.el-input__wrapper {
  border-radius: var(--radius-md) !important;
}
</style>