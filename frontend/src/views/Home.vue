<template>
  <div class="home">
    <!-- Hero 横幅区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">发现你身边的好店铺</h1>
        <p class="hero-subtitle">专为肯恩大学交换生打造的本地商家智能推荐平台</p>
        
        <!-- 搜索框 -->
        <div class="search-box">
          <div class="search-input-wrapper">
            <span class="search-icon">🔍</span>
            <input 
              v-model="searchKeyword"
              type="text"
              placeholder="搜索餐厅、咖啡馆、购物..."
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>

        <!-- 快捷分类标签 -->
        <div class="quick-tags">
          <span class="quick-tag" @click="searchCategory('Restaurants')">🍕 餐厅</span>
          <span class="quick-tag" @click="searchCategory('Coffee')">☕ 咖啡</span>
          <span class="quick-tag" @click="searchCategory('Shopping')">🛍️ 购物</span>
          <span class="quick-tag" @click="searchCategory('Beauty')">💇 美容</span>
          <span class="quick-tag" @click="searchCategory('Nightlife')">🎉 娱乐</span>
        </div>
      </div>
    </section>

    <!-- 快捷筛选卡片 -->
    <section class="filter-cards">
      <div class="filter-card" @click="goToFilter('popular')">
        <span class="filter-icon">🔥</span>
        <div class="filter-info">
          <h3>热门推荐</h3>
          <p>最受欢迎的商家</p>
        </div>
      </div>
      <div class="filter-card" @click="goToFilter('topRated')">
        <span class="filter-icon">⭐</span>
        <div class="filter-info">
          <h3>高分好评</h3>
          <p>4.5星以上商家</p>
        </div>
      </div>
      <div class="filter-card" @click="goToFilter('valueForMoney')">
        <span class="filter-icon">💰</span>
        <div class="filter-info">
          <h3>超值性价比</h3>
          <p>物美价廉之选</p>
        </div>
      </div>
      <div class="filter-card" @click="goToFilter('familyFriendly')">
        <span class="filter-icon">👨‍👩‍👧</span>
        <div class="filter-info">
          <h3>家庭友好</h3>
          <p>适合家庭聚餐</p>
        </div>
      </div>
    </section>

    <!-- 高分推荐商家 -->
    <section class="top-businesses">
      <div class="section-header">
        <h2>⭐ 高分推荐商家</h2>
        <router-link to="/businesses" class="view-all">查看全部 </router-link>
      </div>
      
      <div class="business-grid" v-loading="loading">
        <div 
          v-for="business in topBusinesses" 
          :key="business.businessId"
          class="business-card"
          @click="goToDetail(business.businessId)"
        >
          <div class="card-header">
            <div class="business-avatar">{{ getInitial(business.name) }}</div>
            <div class="business-info">
              <h3 class="business-name">{{ business.name }}</h3>
              <p class="business-location">📍 {{ business.city }}, {{ business.state }}</p>
            </div>
          </div>
          
          <div class="card-rating">
            <div class="stars">
              <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= Math.round(business.stars) }"></span>
            </div>
            <span class="rating-text">{{ business.stars?.toFixed(1) }}</span>
            <span class="review-count">{{ business.reviewCount }} 条评论</span>
          </div>
          
          <div class="card-categories">
            <span 
              v-for="cat in getCategories(business.categories)" 
              :key="cat" 
              class="category-tag"
            >
              {{ cat }}
            </span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { businessApi } from '../api'

export default {
  name: 'Home',
  data() {
    return {
      searchKeyword: '',
      topBusinesses: [],
      loading: false
    }
  },
  mounted() {
    this.loadTopBusinesses()
  },
  methods: {
    async loadTopBusinesses() {
      this.loading = true
      try {
        const res = await businessApi.getTopRated()
        if (res.code === 200) {
          this.topBusinesses = res.data?.slice(0, 8) || []
        }
      } catch (error) {
        console.error('加载高分商家失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      if (this.searchKeyword.trim()) {
        this.$router.push({
          path: '/businesses',
          query: { keyword: this.searchKeyword }
        })
      }
    },
    searchCategory(category) {
      this.$router.push({
        path: '/businesses',
        query: { category }
      })
    },
    goToFilter(filterType) {
      this.$router.push({
        path: '/businesses',
        query: { quickFilter: filterType }
      })
    },
    goToDetail(id) {
      this.$router.push(`/businesses/${id}`)
    },
    getInitial(name) {
      return name ? name.charAt(0).toUpperCase() : '?'
    },
    getCategories(categoriesStr) {
      if (!categoriesStr) return []
      return categoriesStr.split(',').slice(0, 3).map(c => c.trim())
    }
  }
}
</script>

<style scoped>
.home {
  max-width: 100%;
}

/* Hero 区域 */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  padding: 60px 40px;
  text-align: center;
  margin-bottom: 32px;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: pulse 4s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.3; }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: white;
  margin-bottom: 12px;
  letter-spacing: -1px;
}

.hero-subtitle {
  font-size: 18px;
  color: rgba(255,255,255,0.9);
  margin-bottom: 32px;
}

/* 搜索框 */
.search-box {
  max-width: 600px;
  margin: 0 auto 24px;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background: white;
  border-radius: 50px;
  padding: 8px 8px 8px 24px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
}

.search-icon {
  font-size: 20px;
  margin-right: 12px;
}

.search-input-wrapper input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  padding: 12px 0;
}

.search-btn {
  background: linear-gradient(135deg, #e63946 0%, #d62839 100%);
  color: white;
  border: none;
  padding: 14px 32px;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(230,57,70,0.4);
}

/* 快捷标签 */
.quick-tags {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-tag {
  background: rgba(255,255,255,0.2);
  color: white;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  backdrop-filter: blur(4px);
}

.quick-tag:hover {
  background: rgba(255,255,255,0.3);
  transform: translateY(-2px);
}

/* 筛选卡片 */
.filter-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.filter-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.filter-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.12);
}

.filter-icon {
  font-size: 36px;
}

.filter-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #2b2d42;
  margin-bottom: 4px;
}

.filter-info p {
  font-size: 13px;
  color: #8d99ae;
}

/* 商家列表区域 */
.top-businesses {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 22px;
  font-weight: 600;
  color: #2b2d42;
}

.view-all {
  color: #e63946;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.view-all:hover {
  text-decoration: underline;
}

/* 商家卡片网格 */
.business-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.business-card {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.business-card:hover {
  background: #fff;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.business-avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  font-weight: 600;
}

.business-info {
  flex: 1;
  min-width: 0;
}

.business-name {
  font-size: 15px;
  font-weight: 600;
  color: #2b2d42;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.business-location {
  font-size: 12px;
  color: #8d99ae;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  color: #ddd;
  font-size: 14px;
}

.star.filled {
  color: #ffc107;
}

.rating-text {
  font-weight: 600;
  color: #2b2d42;
  font-size: 14px;
}

.review-count {
  font-size: 12px;
  color: #8d99ae;
}

.card-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.category-tag {
  background: #e9ecef;
  color: #495057;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 11px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .filter-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .business-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 28px;
  }
  .filter-cards {
    grid-template-columns: 1fr;
  }
  .business-grid {
    grid-template-columns: 1fr;
  }
}
</style>