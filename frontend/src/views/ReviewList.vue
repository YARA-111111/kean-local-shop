<template>
  <div class="review-list">
    <div class="page-header">
      <h1>💬 用户评论</h1>
      <p>查看真实用户的评价与反馈</p>
    </div>

    <div class="content-card">
      <div class="reviews-container" v-loading="loading">
        <div v-for="review in reviews" :key="review.reviewId" class="review-item">
          <div class="review-header">
            <div class="user-avatar">{{ review.userId?.charAt(0) || 'U' }}</div>
            <div class="review-meta">
              <div class="user-id">用户 {{ review.userId?.substring(0, 8) }}...</div>
              <div class="review-date">{{ formatDate(review.reviewDate) }}</div>
            </div>
            <div class="rating-stars">
              <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= review.stars }"></span>
            </div>
          </div>
          
          <div class="review-content">
            {{ review.text }}
          </div>
          
          <div class="review-footer">
            <span class="reaction">👍 {{ review.useful || 0 }} 有用</span>
            <span class="reaction">😄 {{ review.funny || 0 }} 有趣</span>
            <span class="reaction">😎 {{ review.cool || 0 }} 酷</span>
          </div>
        </div>

        <div v-if="!loading && reviews.length === 0" class="empty-state">
          <span class="empty-icon">💬</span>
          <p>暂无评论数据</p>
        </div>
      </div>

      <div class="pagination" v-if="total > pageSize">
        <button :disabled="currentPage === 1" @click="handlePageChange(currentPage - 1)">上一页</button>
        <span class="page-info">第 {{ currentPage }} / {{ Math.ceil(total / pageSize) }} 页</span>
        <button :disabled="currentPage >= Math.ceil(total / pageSize)" @click="handlePageChange(currentPage + 1)">下一页</button>
      </div>
    </div>
  </div>
</template>

<script>
import { reviewApi } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'ReviewList',
  data() {
    return {
      reviews: [],
      loading: false,
      total: 0,
      currentPage: 1,
      pageSize: 10
    }
  },
  mounted() {
    this.loadReviews()
  },
  methods: {
    async loadReviews() {
      this.loading = true
      try {
        const res = await reviewApi.getAll(this.currentPage - 1, this.pageSize)
        if (res.code === 200) {
          this.reviews = res.data.content || []
          this.total = res.data.totalElements || 0
        }
      } catch (error) {
        ElMessage.error('加载评论失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadReviews()
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
    }
  }
}
</script>

<style scoped>
.review-list {
  max-width: 900px;
  margin: 0 auto;
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

.content-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.review-item {
  padding: 24px 0;
  border-bottom: 1px solid #f1f3f4;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-avatar {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.review-meta {
  flex: 1;
}

.user-id {
  font-weight: 600;
  color: #2b2d42;
  font-size: 15px;
}

.review-date {
  font-size: 13px;
  color: #8d99ae;
}

.rating-stars {
  display: flex;
}

.star {
  color: #ddd;
  font-size: 18px;
}

.star.filled {
  color: #ffc107;
}

.review-content {
  color: #495057;
  line-height: 1.7;
  font-size: 15px;
  margin-bottom: 16px;
}

.review-footer {
  display: flex;
  gap: 20px;
}

.reaction {
  font-size: 13px;
  color: #8d99ae;
}

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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f1f3f4;
}

.pagination button {
  padding: 10px 20px;
  border: 1px solid #e9ecef;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination button:hover:not(:disabled) {
  background: #f8f9fa;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #6c757d;
  font-size: 14px;
}
</style>