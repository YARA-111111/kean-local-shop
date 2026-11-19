<template>
  <div class="business-detail" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="back-nav">
      <button class="back-btn" @click="$router.back()"> 返回列表</button>
    </div>

    <div class="detail-content" v-if="business.businessId">
      <!-- 商家头部信息 -->
      <div class="business-header">
        <div class="header-main">
          <div class="business-avatar">{{ getInitial(business.name) }}</div>
          <div class="header-info">
            <h1>{{ business.name }}</h1>
            <div class="header-meta">
              <span class="location">📍 {{ business.address }}, {{ business.city }}, {{ business.state }} {{ business.postalCode }}</span>
            </div>
            <div class="header-stats">
              <div class="rating-display">
                <div class="stars">
                  <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= Math.round(business.stars || 0) }"></span>
                </div>
                <span class="rating-value">{{ (business.stars || 0).toFixed(1) }}</span>
                <span class="review-count">({{ business.reviewCount || 0 }} 条评论)</span>
              </div>
              <span class="price-tag" v-if="business.priceRange">{{ '$'.repeat(business.priceRange) }}</span>
              <span class="status-tag" :class="{ open: business.isOpen }">
                {{ business.isOpen ? '🟢 营业中' : '🔴 已关闭' }}
              </span>
            </div>
          </div>
        </div>
        
        <!-- 类别标签 -->
        <div class="category-tags">
          <span v-for="cat in categories" :key="cat" class="cat-tag">{{ cat }}</span>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 左侧内容 -->
        <div class="left-column">
          <!-- 商家特性 -->
          <section class="info-section" v-if="hasAnyAttribute">
            <h2>🏷️ 商家特性</h2>
            <div class="feature-grid">
              <div class="feature-item" v-if="hasAttribute('RestaurantsDelivery', 'True')">
                <span class="feature-icon">🚗</span>
                <span>外卖配送</span>
              </div>
              <div class="feature-item" v-if="hasAttribute('RestaurantsTakeOut', 'True')">
                <span class="feature-icon">🥡</span>
                <span>外带</span>
              </div>
              <div class="feature-item" v-if="hasAttribute('RestaurantsReservations', 'True')">
                <span class="feature-icon">📅</span>
                <span>可预订</span>
              </div>
              <div class="feature-item" v-if="hasAttribute('OutdoorSeating', 'True')">
                <span class="feature-icon">🌳</span>
                <span>户外座位</span>
              </div>
              <div class="feature-item" v-if="hasAttribute('GoodForKids', 'True')">
                <span class="feature-icon">👶</span>
                <span>适合儿童</span>
              </div>
              <div class="feature-item" v-if="hasAttribute('WheelchairAccessible', 'True')">
                <span class="feature-icon">♿</span>
                <span>无障碍</span>
              </div>
              <div class="feature-item" v-if="hasAttribute('WiFi', 'free')">
                <span class="feature-icon">📶</span>
                <span>免费WiFi</span>
              </div>
              <div class="feature-item" v-if="hasAttribute('HasTV', 'True')">
                <span class="feature-icon">📺</span>
                <span>有电视</span>
              </div>
            </div>
          </section>

          <!-- 营业时间 -->
          <section class="info-section" v-if="business.businessHours && business.businessHours.length > 0">
            <h2>🕐 营业时间</h2>
            <div class="hours-list">
              <div v-for="hour in business.businessHours" :key="hour.dayOfWeek" class="hour-item">
                <span class="day">{{ hour.dayOfWeek }}</span>
                <span class="time" v-if="hour.isClosed">休息</span>
                <span class="time" v-else>{{ hour.openTime }} - {{ hour.closeTime }}</span>
              </div>
            </div>
          </section>

          <!-- 评分分布 -->
          <section class="info-section" v-if="business.ratingDistribution">
            <h2>📊 评分分布</h2>
            <div class="rating-distribution">
              <div v-for="star in [5, 4, 3, 2, 1]" :key="star" class="rating-bar">
                <span class="star-label">{{ star }}星</span>
                <div class="bar-bg">
                  <div class="bar-fill" :style="{ width: getRatingPercentage(star) + '%', background: getProgressColor(star) }"></div>
                </div>
                <span class="count-label">{{ business.ratingDistribution[star] || 0 }}</span>
              </div>
            </div>
          </section>

          <!-- 评分趋势 -->
          <section class="info-section" v-if="recentTrendData.length > 0">
            <div class="section-header">
              <h2>📈 评分趋势</h2>
              <span class="trend-badge" :class="getTrendClass(ratingTrend.overallTrend)">
                {{ getTrendLabel(ratingTrend.overallTrend) }}
              </span>
            </div>
            <p class="trend-info">平均每月 {{ ratingTrend.avgMonthlyReviews?.toFixed(1) || 0 }} 条评论<span v-if="ratingTrend.trendData && ratingTrend.trendData.length > 12"> · 显示最近 12 个月</span></p>
            <div class="chart-container">
              <div class="chart-bars">
                <div v-for="(item, index) in recentTrendData" :key="index" class="chart-bar-wrapper">
                  <div class="bar-container">
                    <div class="bar" :style="{ height: getBarHeight(item.avgRating) + 'px' }"></div>
                    <div class="bar-value">{{ parseFloat(item.avgRating).toFixed(1) }}</div>
                  </div>
                  <div class="bar-label">{{ item.period }}</div>
                </div>
              </div>
            </div>
          </section>
        </div>

        <!-- 右侧内容 -->
        <div class="right-column">
          <!-- 用户评论 -->
          <section class="info-section reviews-section">
            <div class="section-header">
              <h2>💬 用户评论</h2>
              <span class="count-badge">{{ reviewTotal || business.reviewCount || 0 }} 条</span>
            </div>
            
            <div v-if="reviews.length === 0" class="empty-state">
              <span class="empty-icon">💬</span>
              <p v-if="business.reviewCount > 0">该商家共有 {{ business.reviewCount }} 条评论，详细内容暂未收录</p>
              <p v-else>暂无评论</p>
            </div>
            
            <div v-else class="reviews-list">
              <div v-for="review in reviews" :key="review.reviewId" class="review-card">
                <div class="review-header">
                  <div class="reviewer-avatar">{{ review.userId?.charAt(0) || 'U' }}</div>
                  <div class="reviewer-info">
                    <div class="reviewer-name">用户 {{ review.userId?.substring(0, 8) }}...</div>
                    <div class="review-date">{{ formatDate(review.reviewDate) }}</div>
                  </div>
                  <div class="review-stars">
                    <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= review.stars }"></span>
                  </div>
                </div>
                <p class="review-text">{{ review.text }}</p>
                <div class="review-reactions">
                  <span class="reaction">👍 {{ review.useful || 0 }}</span>
                  <span class="reaction">😄 {{ review.funny || 0 }}</span>
                  <span class="reaction">😎 {{ review.cool || 0 }}</span>
                </div>
              </div>
              
              <!-- 查看更多按钮 -->
              <button v-if="reviewTotal > 5" class="view-more-btn" @click="openReviewModal">
                查看全部 {{ reviewTotal }} 条评论
              </button>
            </div>
          </section>

          <!-- 用户小贴士 -->
          <section class="info-section tips-section">
            <div class="section-header">
              <h2>💡 用户小贴士</h2>
              <span class="count-badge">{{ tipsTotal || tips.length }} 条</span>
            </div>
            
            <div v-if="tips.length === 0" class="empty-state">
              <span class="empty-icon">💡</span>
              <p>暂无小贴士数据</p>
            </div>
            
            <div v-else class="tips-list">
              <div v-for="tip in tips" :key="tip.id" class="tip-card">
                <p class="tip-text">{{ tip.text }}</p>
                <div class="tip-footer">
                  <span class="tip-date">{{ formatDate(tip.tipDate) }}</span>
                  <span class="tip-likes">❤️ {{ tip.complimentCount || 0 }}</span>
                </div>
              </div>
              
              <!-- 查看更多按钮 -->
              <button v-if="tipsTotal > 5" class="view-more-btn" @click="openTipModal">
                查看全部 {{ tipsTotal }} 条小贴士
              </button>
            </div>
          </section>
        </div>
      </div>
    </div>

    <!-- 评论模态框 -->
    <div class="modal-overlay" v-if="showReviewModal" @click.self="closeReviewModal">
      <div class="modal-container">
        <div class="modal-header">
          <h3>💬 全部评论</h3>
          <span class="modal-count">{{ modalReviewTotal }} 条</span>
          <button class="modal-close" @click="closeReviewModal">×</button>
        </div>
        <div class="modal-body" @scroll="handleReviewScroll">
          <div v-for="review in modalReviews" :key="review.reviewId" class="review-card">
            <div class="review-header">
              <div class="reviewer-avatar">{{ review.userId?.charAt(0) || 'U' }}</div>
              <div class="reviewer-info">
                <div class="reviewer-name">用户 {{ review.userId?.substring(0, 8) }}...</div>
                <div class="review-date">{{ formatDate(review.reviewDate) }}</div>
              </div>
              <div class="review-stars">
                <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= review.stars }"></span>
              </div>
            </div>
            <p class="review-text">{{ review.text }}</p>
            <div class="review-reactions">
              <span class="reaction">👍 {{ review.useful || 0 }}</span>
              <span class="reaction">😄 {{ review.funny || 0 }}</span>
              <span class="reaction">😎 {{ review.cool || 0 }}</span>
            </div>
          </div>
          <div v-if="modalReviewLoading" class="loading-more">加载中...</div>
          <div v-if="!modalReviewLoading && modalReviewPage < modalReviewTotalPages" class="load-more-hint">滚动加载更多</div>
        </div>
      </div>
    </div>

    <!-- 小贴士模态框 -->
    <div class="modal-overlay" v-if="showTipModal" @click.self="closeTipModal">
      <div class="modal-container">
        <div class="modal-header">
          <h3>💡 全部小贴士</h3>
          <span class="modal-count">{{ modalTipTotal }} 条</span>
          <button class="modal-close" @click="closeTipModal">×</button>
        </div>
        <div class="modal-body" @scroll="handleTipScroll">
          <div v-for="tip in modalTips" :key="tip.id" class="tip-card">
            <p class="tip-text">{{ tip.text }}</p>
            <div class="tip-footer">
              <span class="tip-date">{{ formatDate(tip.tipDate) }}</span>
              <span class="tip-likes">❤️ {{ tip.complimentCount || 0 }}</span>
            </div>
          </div>
          <div v-if="modalTipLoading" class="loading-more">加载中...</div>
          <div v-if="!modalTipLoading && modalTipPage < modalTipTotalPages" class="load-more-hint">滚动加载更多</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { businessApi, reviewApi, tipApi } from '../api'

export default {
  name: 'BusinessDetail',
  data() {
    return {
      business: {},
      reviews: [],
      tips: [],
      ratingTrend: {},
      loading: false,
      // 默认显示5条
      reviewTotal: 0,
      tipsTotal: 0,
      // 评论模态框
      showReviewModal: false,
      modalReviews: [],
      modalReviewPage: 1,
      modalReviewTotal: 0,
      modalReviewLoading: false,
      // 小贴士模态框
      showTipModal: false,
      modalTips: [],
      modalTipPage: 1,
      modalTipTotal: 0,
      modalTipLoading: false
    }
  },
  computed: {
    categories() {
      if (this.business.categoryList && this.business.categoryList.length > 0) {
        return this.business.categoryList
      }
      return (this.business.categories || '').split(',').map(c => c.trim()).filter(c => c)
    },
    totalRatings() {
      if (!this.business.ratingDistribution) return 0
      return Object.values(this.business.ratingDistribution).reduce((a, b) => a + b, 0)
    },
    hasAnyAttribute() {
      return this.business.attributes && Object.keys(this.business.attributes).length > 0
    },
    modalReviewTotalPages() {
      return Math.ceil(this.modalReviewTotal / 10)
    },
    modalTipTotalPages() {
      return Math.ceil(this.modalTipTotal / 10)
    },
    // 只显示最近12个月的趋势数据
    recentTrendData() {
      if (!this.ratingTrend.trendData) return []
      const data = this.ratingTrend.trendData
      return data.length > 12 ? data.slice(-12) : data
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const id = this.$route.params.id
      this.loading = true
      try {
        const [businessRes, reviewsRes, tipsRes, trendRes] = await Promise.all([
          businessApi.getById(id),
          reviewApi.getByBusinessId(id, 0, 5),
          tipApi.getByBusinessId(id, 0, 5),
          businessApi.getRatingTrend(id)
        ])
        if (businessRes.code === 200) this.business = businessRes.data
        if (reviewsRes.code === 200) {
          this.reviews = reviewsRes.data.content || []
          this.reviewTotal = reviewsRes.data.totalElements || 0
        }
        if (tipsRes.code === 200) {
          this.tips = tipsRes.data.content || []
          this.tipsTotal = tipsRes.data.totalElements || 0
        }
        if (trendRes.code === 200) this.ratingTrend = trendRes.data || {}
      } catch (error) {
        console.error('加载数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    // 评论模态框
    async openReviewModal() {
      this.showReviewModal = true
      this.modalReviews = []
      this.modalReviewPage = 1
      this.modalReviewTotal = this.reviewTotal
      document.body.style.overflow = 'hidden'
      await this.loadModalReviews()
    },
    closeReviewModal() {
      this.showReviewModal = false
      document.body.style.overflow = ''
    },
    async loadModalReviews() {
      if (this.modalReviewLoading) return
      this.modalReviewLoading = true
      const id = this.$route.params.id
      try {
        const res = await reviewApi.getByBusinessId(id, this.modalReviewPage - 1, 10)
        if (res.code === 200) {
          this.modalReviews = [...this.modalReviews, ...(res.data.content || [])]
          this.modalReviewTotal = res.data.totalElements || 0
        }
      } catch (error) {
        console.error('加载评论失败:', error)
      } finally {
        this.modalReviewLoading = false
      }
    },
    handleReviewScroll(e) {
      const { scrollTop, scrollHeight, clientHeight } = e.target
      if (scrollTop + clientHeight >= scrollHeight - 50) {
        if (this.modalReviewPage < this.modalReviewTotalPages && !this.modalReviewLoading) {
          this.modalReviewPage++
          this.loadModalReviews()
        }
      }
    },
    // 小贴士模态框
    async openTipModal() {
      this.showTipModal = true
      this.modalTips = []
      this.modalTipPage = 1
      this.modalTipTotal = this.tipsTotal
      document.body.style.overflow = 'hidden'
      await this.loadModalTips()
    },
    closeTipModal() {
      this.showTipModal = false
      document.body.style.overflow = ''
    },
    async loadModalTips() {
      if (this.modalTipLoading) return
      this.modalTipLoading = true
      const id = this.$route.params.id
      try {
        const res = await tipApi.getByBusinessId(id, this.modalTipPage - 1, 10)
        if (res.code === 200) {
          this.modalTips = [...this.modalTips, ...(res.data.content || [])]
          this.modalTipTotal = res.data.totalElements || 0
        }
      } catch (error) {
        console.error('加载小贴士失败:', error)
      } finally {
        this.modalTipLoading = false
      }
    },
    handleTipScroll(e) {
      const { scrollTop, scrollHeight, clientHeight } = e.target
      if (scrollTop + clientHeight >= scrollHeight - 50) {
        if (this.modalTipPage < this.modalTipTotalPages && !this.modalTipLoading) {
          this.modalTipPage++
          this.loadModalTips()
        }
      }
    },
    getInitial(name) {
      return name ? name.charAt(0).toUpperCase() : '?'
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' })
    },
    hasAttribute(name, expectedValue) {
      if (!this.business.attributes) return false
      const value = this.business.attributes[name]
      if (!value) return false
      return value.toLowerCase().includes(expectedValue.toLowerCase())
    },
    getRatingPercentage(star) {
      if (!this.totalRatings) return 0
      const count = this.business.ratingDistribution[star] || 0
      return Math.round((count / this.totalRatings) * 100)
    },
    getProgressColor(star) {
      const colors = { 5: '#22c55e', 4: '#84cc16', 3: '#eab308', 2: '#f97316', 1: '#ef4444' }
      return colors[star] || '#6366f1'
    },
    getTrendClass(trend) {
      return { 'RISING': 'rising', 'DECLINING': 'declining', 'STABLE': 'stable' }[trend] || 'stable'
    },
    getTrendLabel(trend) {
      return { 'RISING': '�� 上升', 'DECLINING': '📉 下降', 'STABLE': '➡️ 稳定' }[trend] || trend
    },
    getBarHeight(rating) {
      return (parseFloat(rating) / 5) * 100
    }
  }
}
</script>

<style scoped>
.business-detail {
  max-width: 100%;
}

.back-nav {
  margin-bottom: 20px;
}

.back-btn {
  background: none;
  border: none;
  color: #6366f1;
  font-size: 15px;
  cursor: pointer;
  padding: 8px 0;
}

.back-btn:hover {
  text-decoration: underline;
}

/* 商家头部 */
.business-header {
  background: white;
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.header-main {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
}

.business-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 36px;
  font-weight: 700;
  flex-shrink: 0;
}

.header-info h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.header-meta {
  margin-bottom: 12px;
}

.location {
  color: #6b7280;
  font-size: 14px;
}

.header-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars {
  display: flex;
}

.star {
  color: #d1d5db;
  font-size: 18px;
}

.star.filled {
  color: #fbbf24;
}

.rating-value {
  font-weight: 700;
  font-size: 18px;
  color: #1f2937;
}

.review-count {
  color: #6b7280;
  font-size: 14px;
}

.price-tag {
  background: #fef3c7;
  color: #92400e;
  padding: 4px 12px;
  border-radius: 8px;
  font-weight: 600;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 13px;
  background: #fee2e2;
  color: #991b1b;
}

.status-tag.open {
  background: #dcfce7;
  color: #166534;
}

.category-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.cat-tag {
  background: #f3f4f6;
  color: #4b5563;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
}

/* 主内容区 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 24px;
}

.info-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.info-section h2 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  margin-bottom: 0;
}

.count-badge {
  background: #e0e7ff;
  color: #4338ca;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

/* 特性网格 */
.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 10px;
}

.feature-icon {
  font-size: 20px;
}

/* 营业时间 */
.hours-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.hour-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 12px;
  background: #f9fafb;
  border-radius: 8px;
}

.day {
  font-weight: 500;
  color: #374151;
}

.time {
  color: #6b7280;
}

/* 评分分布 */
.rating-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.star-label {
  width: 36px;
  font-size: 13px;
  color: #6b7280;
}

.bar-bg {
  flex: 1;
  height: 10px;
  background: #e5e7eb;
  border-radius: 5px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 5px;
  transition: width 0.3s;
}

.count-label {
  width: 32px;
  text-align: right;
  font-size: 13px;
  color: #9ca3af;
}

/* 趋势图 */
.trend-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
}

.trend-badge.rising {
  background: #dcfce7;
  color: #166534;
}

.trend-badge.declining {
  background: #fee2e2;
  color: #991b1b;
}

.trend-badge.stable {
  background: #e0e7ff;
  color: #4338ca;
}

.trend-info {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 16px;
}

.chart-container {
  overflow-x: auto;
}

.chart-bars {
  display: flex;
  gap: 16px;
  padding: 10px 0;
}

.chart-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 50px;
}

.bar-container {
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
}

.bar {
  width: 24px;
  background: linear-gradient(to top, #6366f1, #8b5cf6);
  border-radius: 4px 4px 0 0;
}

.bar-value {
  font-size: 11px;
  color: #6366f1;
  font-weight: 600;
  margin-top: 4px;
}

.bar-label {
  font-size: 10px;
  color: #9ca3af;
  margin-top: 6px;
}

/* 评论区 */
.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  padding: 20px;
  background: #f9fafb;
  border-radius: 12px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
}

.reviewer-info {
  flex: 1;
}

.reviewer-name {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.review-date {
  font-size: 12px;
  color: #9ca3af;
}

.review-stars .star {
  font-size: 14px;
}

.review-text {
  color: #4b5563;
  line-height: 1.6;
  font-size: 14px;
  margin-bottom: 12px;
}

.review-reactions {
  display: flex;
  gap: 16px;
}

.reaction {
  font-size: 13px;
  color: #9ca3af;
}

/* 小贴士 */
.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tip-card {
  padding: 16px;
  background: linear-gradient(135deg, #fef9e7 0%, #fef3cd 100%);
  border-radius: 12px;
  border-left: 4px solid #f59e0b;
}

.tip-text {
  color: #4b5563;
  line-height: 1.6;
  font-size: 14px;
  margin-bottom: 10px;
}

.tip-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #9ca3af;
}

.tip-likes {
  color: #ef4444;
}

/* 查看更多按钮 */
.view-more-btn {
  width: 100%;
  padding: 14px 20px;
  margin-top: 16px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.view-more-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 700px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.modal-count {
  margin-left: 12px;
  background: #e0e7ff;
  color: #4338ca;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.modal-close {
  margin-left: auto;
  width: 36px;
  height: 36px;
  border: none;
  background: #f3f4f6;
  border-radius: 50%;
  font-size: 24px;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #e5e7eb;
  color: #1f2937;
}

.modal-body {
  padding: 20px 24px;
  overflow-y: auto;
  flex: 1;
}

.modal-body .review-card,
.modal-body .tip-card {
  margin-bottom: 16px;
}

.modal-body .review-card:last-child,
.modal-body .tip-card:last-child {
  margin-bottom: 0;
}

.loading-more,
.load-more-hint {
  text-align: center;
  padding: 16px;
  color: #9ca3af;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px;
  color: #9ca3af;
}

.empty-icon {
  font-size: 40px;
  display: block;
  margin-bottom: 12px;
}

/* 分页控件 */
.pagination-wrapper {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.pagination-info {
  text-align: center;
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 12px;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.page-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #e5e7eb;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #f3f4f6;
  border-color: #6366f1;
  color: #6366f1;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-num {
  min-width: 36px;
  height: 36px;
  border: 1px solid #e5e7eb;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
  transition: all 0.2s;
}

.page-num:hover:not(:disabled):not(.active) {
  background: #f3f4f6;
  border-color: #6366f1;
}

.page-num.active {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  border-color: transparent;
}

.page-num.dots {
  border: none;
  background: transparent;
  cursor: default;
  color: #9ca3af;
}

/* 响应式 */
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .header-main {
    flex-direction: column;
    text-align: center;
  }
  .business-avatar {
    margin: 0 auto;
  }
  .feature-grid {
    grid-template-columns: 1fr;
  }
}
</style>