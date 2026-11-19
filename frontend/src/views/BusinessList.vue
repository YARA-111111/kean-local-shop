<template>
  <div class="business-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>🏪 探索商家</h1>
      <p>发现周边优质店铺，找到你的心仪之选</p>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="filter-section">
      <div class="search-row">
        <div class="search-wrapper">
          <span class="search-icon">🔍</span>
          <input 
            v-model="queryParams.keyword" 
            type="text"
            placeholder="搜索商家名称、地址或类别..."
            @keyup.enter="handleSearch"
          />
        </div>
        <button class="search-btn" @click="handleSearch">搜索</button>
      </div>

      <!-- 快捷筛选标签 -->
      <div class="quick-filters">
        <span 
          v-for="filter in quickFilters" 
          :key="filter.key"
          class="filter-tag"
          :class="{ active: activeQuickFilter === filter.key }"
          @click="quickFilter(filter.key)"
        >
          {{ filter.icon }} {{ filter.label }}
        </span>
      </div>

      <!-- 高级筛选 -->
      <div class="advanced-toggle" @click="showAdvanced = !showAdvanced">
        <span>高级筛选</span>
        <span>{{ showAdvanced ? '' : '' }}</span>
      </div>
      
      <transition name="slide">
        <div v-if="showAdvanced" class="advanced-filters">
          <div class="filter-grid">
            <div class="filter-item">
              <label>城市</label>
              <input v-model="queryParams.city" placeholder="输入城市" />
            </div>
            <div class="filter-item">
              <label>价格区间</label>
              <select v-model="queryParams.priceRange">
                <option :value="null">全部</option>
                <option :value="1">$ 经济</option>
                <option :value="2">$$ 适中</option>
                <option :value="3">$$$ 较高</option>
                <option :value="4">$$$$ 奢华</option>
              </select>
            </div>
            <div class="filter-item">
              <label>最低评分</label>
              <select v-model="queryParams.minStars">
                <option :value="null">全部</option>
                <option :value="3">3星以上</option>
                <option :value="3.5">3.5星以上</option>
                <option :value="4">4星以上</option>
                <option :value="4.5">4.5星以上</option>
              </select>
            </div>
            <div class="filter-item">
              <label>排序方式</label>
              <select v-model="queryParams.sortBy">
                <option value="stars">评分</option>
                <option value="reviewCount">评论数</option>
                <option value="popularity">热度</option>
              </select>
            </div>
          </div>
          <div class="facility-filters">
            <label class="checkbox-label" v-for="facility in facilities" :key="facility.key">
              <input type="checkbox" v-model="queryParams[facility.key]" />
              <span>{{ facility.label }}</span>
            </label>
          </div>
          <div class="filter-actions">
            <button class="btn-reset" @click="resetSearch">重置</button>
            <button class="btn-search" @click="handleSearch">应用筛选</button>
          </div>
        </div>
      </transition>
    </div>

    <!-- 结果统计 -->
    <div class="results-info">
      <span>共找到 <strong>{{ total }}</strong> 家商家</span>
    </div>

    <!-- 商家卡片列表 -->
    <div class="business-grid" v-loading="loading">
      <div 
        v-for="business in businesses" 
        :key="business.businessId"
        class="business-card"
        @click="goToDetail(business.businessId)"
      >
        <div class="card-top">
          <div class="business-avatar">{{ getInitial(business.name) }}</div>
          <div class="business-main-info">
            <h3>{{ business.name }}</h3>
            <p class="location">📍 {{ business.city }}, {{ business.state }}</p>
          </div>
          <div class="price-tag" v-if="business.priceRange">
            {{ '$'.repeat(business.priceRange) }}
          </div>
        </div>
        
        <div class="card-middle">
          <div class="rating-row">
            <div class="stars">
              <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= Math.round(business.stars || 0) }"></span>
            </div>
            <span class="rating-value">{{ (business.stars || 0).toFixed(1) }}</span>
            <span class="review-count">({{ business.reviewCount || 0 }})</span>
          </div>
          
          <div class="facilities" v-if="hasFacilities(business)">
            <span v-if="business.hasDelivery" class="facility-icon" title="外卖">🚗</span>
            <span v-if="business.hasTakeOut" class="facility-icon" title="外带">🥡</span>
            <span v-if="business.hasOutdoorSeating" class="facility-icon" title="户外座位">🌳</span>
            <span v-if="business.goodForKids" class="facility-icon" title="适合儿童">👶</span>
          </div>
        </div>
        
        <div class="card-bottom">
          <div class="categories">
            <span v-for="cat in getCategories(business.categories)" :key="cat" class="cat-tag">
              {{ cat }}
            </span>
          </div>
          <span class="status-tag" :class="{ open: business.isOpen }">
            {{ business.isOpen ? '营业中' : '已关闭' }}
          </span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && businesses.length === 0" class="empty-state">
      <span class="empty-icon">🔍</span>
      <p>暂无符合条件的商家</p>
      <button @click="resetSearch">清除筛选</button>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > pageSize">
      <button :disabled="currentPage === 1" @click="handlePageChange(currentPage - 1)">上一页</button>
      <span class="page-info">第 {{ currentPage }} / {{ Math.ceil(total / pageSize) }} 页</span>
      <button :disabled="currentPage >= Math.ceil(total / pageSize)" @click="handlePageChange(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script>
import { businessApi } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'BusinessList',
  data() {
    return {
      businesses: [],
      loading: false,
      total: 0,
      currentPage: 1,
      pageSize: 12,
      showAdvanced: false,
      activeQuickFilter: '',
      queryParams: {
        keyword: '',
        city: '',
        priceRange: null,
        minStars: null,
        hasDelivery: false,
        hasTakeOut: false,
        hasReservation: false,
        hasOutdoorSeating: false,
        goodForKids: false,
        wheelchairAccessible: false,
        hasWifi: false,
        sortBy: 'stars',
        sortDir: 'desc'
      },
      quickFilters: [
        { key: 'popular', icon: '🔥', label: '热门推荐' },
        { key: 'topRated', icon: '⭐', label: '高分好评' },
        { key: 'valueForMoney', icon: '💰', label: '性价比' },
        { key: 'familyFriendly', icon: '👨‍👩‍👧', label: '家庭友好' },
        { key: 'highEnd', icon: '✨', label: '高端餐厅' }
      ],
      facilities: [
        { key: 'hasDelivery', label: '外卖配送' },
        { key: 'hasTakeOut', label: '外带' },
        { key: 'hasReservation', label: '可预订' },
        { key: 'hasOutdoorSeating', label: '户外座位' },
        { key: 'goodForKids', label: '适合儿童' },
        { key: 'hasWifi', label: '免费WiFi' }
      ]
    }
  },
  mounted() {
    const { keyword, category, quickFilter } = this.$route.query
    if (keyword) {
      this.queryParams.keyword = keyword
    }
    if (category) {
      this.queryParams.keyword = category
    }
    if (quickFilter) {
      this.quickFilter(quickFilter)
    } else {
      this.loadBusinesses()
    }
  },
  methods: {
    async loadBusinesses() {
      this.loading = true
      this.activeQuickFilter = ''
      try {
        const params = {
          ...this.queryParams,
          page: this.currentPage - 1,
          size: this.pageSize,
          hasDelivery: this.queryParams.hasDelivery || null,
          hasTakeOut: this.queryParams.hasTakeOut || null,
          hasReservation: this.queryParams.hasReservation || null,
          hasOutdoorSeating: this.queryParams.hasOutdoorSeating || null,
          goodForKids: this.queryParams.goodForKids || null,
          wheelchairAccessible: this.queryParams.wheelchairAccessible || null,
          hasWifi: this.queryParams.hasWifi || null
        }
        const res = await businessApi.query(params)
        if (res.code === 200) {
          this.businesses = res.data.content || []
          this.total = res.data.totalElements || 0
        }
      } catch (error) {
        ElMessage.error('加载商家列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadBusinesses()
    },
    resetSearch() {
      this.queryParams = {
        keyword: '',
        city: '',
        priceRange: null,
        minStars: null,
        hasDelivery: false,
        hasTakeOut: false,
        hasReservation: false,
        hasOutdoorSeating: false,
        goodForKids: false,
        wheelchairAccessible: false,
        hasWifi: false,
        sortBy: 'stars',
        sortDir: 'desc'
      }
      this.currentPage = 1
      this.loadBusinesses()
    },
    async quickFilter(type) {
      this.loading = true
      this.activeQuickFilter = type
      try {
        let res
        switch (type) {
          case 'popular':
            res = await businessApi.getPopular(20)
            break
          case 'topRated':
            res = await businessApi.getTopRated(4.0, 20)
            break
          case 'valueForMoney':
            res = await businessApi.getValueForMoney(20)
            break
          case 'familyFriendly':
            res = await businessApi.getFamilyFriendly(20)
            break
          case 'highEnd':
            res = await businessApi.getHighEnd(20)
            break
        }
        if (res && res.code === 200) {
          this.businesses = res.data || []
          this.total = (res.data || []).length
        }
      } catch (error) {
        ElMessage.error('加载失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadBusinesses()
    },
    goToDetail(id) {
      this.$router.push(`/businesses/${id}`)
    },
    getInitial(name) {
      return name ? name.charAt(0).toUpperCase() : '?'
    },
    getCategories(str) {
      if (!str) return []
      return str.split(',').slice(0, 2).map(s => s.trim())
    },
    hasFacilities(business) {
      return business.hasDelivery || business.hasTakeOut || business.hasOutdoorSeating || business.goodForKids
    }
  }
}
</script>

<style scoped>
.business-list {
  max-width: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #2b2d42;
  margin-bottom: 8px;
}

.page-header p {
  color: #8d99ae;
  font-size: 15px;
}

/* 筛选区域 */
.filter-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.search-row {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 0 16px;
}

.search-icon {
  font-size: 18px;
  margin-right: 12px;
}

.search-wrapper input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 14px 0;
  font-size: 15px;
  outline: none;
}

.search-btn {
  background: linear-gradient(135deg, #e63946 0%, #d62839 100%);
  color: white;
  border: none;
  padding: 14px 32px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(230,57,70,0.3);
}

/* 快捷筛选 */
.quick-filters {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.filter-tag {
  padding: 8px 16px;
  background: #f8f9fa;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tag:hover {
  background: #e9ecef;
}

.filter-tag.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

/* 高级筛选 */
.advanced-toggle {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-top: 1px solid #e9ecef;
  cursor: pointer;
  color: #6c757d;
  font-size: 14px;
}

.advanced-filters {
  padding-top: 20px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.filter-item label {
  display: block;
  font-size: 13px;
  color: #6c757d;
  margin-bottom: 6px;
}

.filter-item input,
.filter-item select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.filter-item input:focus,
.filter-item select:focus {
  border-color: #667eea;
}

.facility-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  cursor: pointer;
}

.filter-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-reset {
  padding: 10px 24px;
  border: 1px solid #e9ecef;
  background: white;
  border-radius: 8px;
  cursor: pointer;
}

.btn-search {
  padding: 10px 24px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

/* 结果信息 */
.results-info {
  margin-bottom: 20px;
  color: #6c757d;
  font-size: 14px;
}

/* 商家网格 */
.business-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  min-height: 200px;
}

.business-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.business-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.12);
}

.card-top {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.business-avatar {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 22px;
  font-weight: 600;
  flex-shrink: 0;
}

.business-main-info {
  flex: 1;
  min-width: 0;
}

.business-main-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #2b2d42;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.location {
  font-size: 13px;
  color: #8d99ae;
}

.price-tag {
  background: #fef3cd;
  color: #856404;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.card-middle {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.rating-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stars {
  display: flex;
}

.star {
  color: #ddd;
  font-size: 16px;
}

.star.filled {
  color: #ffc107;
}

.rating-value {
  font-weight: 700;
  color: #2b2d42;
}

.review-count {
  font-size: 13px;
  color: #8d99ae;
}

.facilities {
  display: flex;
  gap: 4px;
}

.facility-icon {
  font-size: 16px;
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.categories {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.cat-tag {
  background: #e9ecef;
  color: #495057;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 11px;
}

.status-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  background: #f8d7da;
  color: #721c24;
}

.status-tag.open {
  background: #d4edda;
  color: #155724;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px;
  color: #8d99ae;
}

.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 16px;
}

.empty-state button {
  margin-top: 16px;
  padding: 10px 24px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
}

.pagination button {
  padding: 10px 20px;
  border: 1px solid #e9ecef;
  background: white;
  border-radius: 8px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #6c757d;
  font-size: 14px;
}

/* 动画 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s;
}

.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式 */
@media (max-width: 1200px) {
  .business-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .filter-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .business-grid {
    grid-template-columns: 1fr;
  }
  .filter-grid {
    grid-template-columns: 1fr;
  }
}
</style>