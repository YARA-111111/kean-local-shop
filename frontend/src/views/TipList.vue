<template>
  <div class="tip-list">
    <div class="page-header">
      <h1>💡 小贴士</h1>
      <p>来自用户的实用建议和心得分享</p>
    </div>

    <div class="content-card">
      <div class="tips-container" v-loading="loading">
        <div v-for="tip in tips" :key="tip.id" class="tip-item">
          <div class="tip-header">
            <div class="user-avatar">{{ tip.userId?.charAt(0) || 'U' }}</div>
            <div class="tip-meta">
              <div class="user-id">用户 {{ tip.userId?.substring(0, 8) }}...</div>
              <div class="tip-date">{{ formatDate(tip.tipDate) }}</div>
            </div>
            <div class="compliment-badge" v-if="tip.complimentCount > 0">
              ❤️ {{ tip.complimentCount }}
            </div>
          </div>
          
          <div class="tip-content">
            {{ tip.text }}
          </div>
        </div>

        <div v-if="!loading && tips.length === 0" class="empty-state">
          <span class="empty-icon">💡</span>
          <p>暂无小贴士数据</p>
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
import { tipApi } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'TipList',
  data() {
    return {
      tips: [],
      loading: false,
      total: 0,
      currentPage: 1,
      pageSize: 10
    }
  },
  mounted() {
    this.loadTips()
  },
  methods: {
    async loadTips() {
      this.loading = true
      try {
        const res = await tipApi.getAll(this.currentPage - 1, this.pageSize)
        if (res.code === 200) {
          this.tips = res.data.content || []
          this.total = res.data.totalElements || 0
        }
      } catch (error) {
        ElMessage.error('加载小贴士失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadTips()
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
.tip-list {
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

.tip-item {
  padding: 20px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #fef9e7 0%, #fef3cd 100%);
  border-radius: 12px;
  border-left: 4px solid #f4a261;
}

.tip-item:last-child {
  margin-bottom: 0;
}

.tip-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #f4a261 0%, #e76f51 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  font-weight: 600;
}

.tip-meta {
  flex: 1;
}

.user-id {
  font-weight: 600;
  color: #2b2d42;
  font-size: 14px;
}

.tip-date {
  font-size: 12px;
  color: #8d99ae;
}

.compliment-badge {
  background: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  color: #e63946;
}

.tip-content {
  color: #495057;
  line-height: 1.6;
  font-size: 15px;
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