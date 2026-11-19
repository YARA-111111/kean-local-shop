import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// 商家API
export const businessApi = {
  // 高级条件查询商家列表
  query: (params) => {
    const query = new URLSearchParams()
    Object.keys(params).forEach(key => {
      if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
        query.append(key, params[key])
      }
    })
    return api.get(`/businesses?${query.toString()}`)
  },

  // 获取所有商家 (兼容旧接口)
  getAll: (page = 0, size = 10, sortBy = 'stars', sortDir = 'desc') => 
    api.get(`/businesses?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`),
  
  // 获取单个商家详情
  getById: (id) => api.get(`/businesses/${id}`),
  
  // 创建商家
  create: (data) => api.post('/businesses', data),
  
  // 更新商家
  update: (id, data) => api.put(`/businesses/${id}`, data),
  
  // 删除商家
  delete: (id) => api.delete(`/businesses/${id}`),
  
  // 搜索商家 (兼容旧接口)
  search: (keyword, page = 0, size = 10) => 
    api.get(`/businesses?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`),
  
  // 获取热门商家
  getPopular: (limit = 10) => api.get(`/businesses/popular?limit=${limit}`),
  
  // 获取高评分商家
  getTopRated: (minStars = 4.0, limit = 10) => 
    api.get(`/businesses/top-rated?minStars=${minStars}&limit=${limit}`),
  
  // 获取性价比商家
  getValueForMoney: (limit = 10) => api.get(`/businesses/value-for-money?limit=${limit}`),
  
  // 获取家庭友好型商家
  getFamilyFriendly: (limit = 10) => api.get(`/businesses/family-friendly?limit=${limit}`),
  
  // 获取高端餐厅
  getHighEnd: (limit = 10) => api.get(`/businesses/high-end?limit=${limit}`),
  
  // 按类别查找
  findByCategory: (category, page = 0, size = 10) => 
    api.get(`/businesses/category/${encodeURIComponent(category)}?page=${page}&size=${size}`),
  
  // 查找附近商家
  findNearby: (lat, lng, radiusKm = 5, limit = 10) => 
    api.get(`/businesses/nearby?lat=${lat}&lng=${lng}&radiusKm=${radiusKm}&limit=${limit}`),
  
  // 获取评分趋势
  getRatingTrend: (id) => api.get(`/businesses/${id}/rating-trend`)
}

// 评论API
export const reviewApi = {
  // 获取所有评论
  getAll: (page = 0, size = 10) => api.get(`/reviews?page=${page}&size=${size}`),
  
  // 获取单个评论
  getById: (id) => api.get(`/reviews/${id}`),
  
  // 获取商家评论
  getByBusinessId: (businessId, page = 0, size = 10) => 
    api.get(`/reviews/business/${businessId}?page=${page}&size=${size}`),
  
  // 创建评论
  create: (data) => api.post('/reviews', data),
  
  // 更新评论
  update: (id, data) => api.put(`/reviews/${id}`, data),
  
  // 删除评论
  delete: (id) => api.delete(`/reviews/${id}`)
}

// 提示API
export const tipApi = {
  // 获取所有提示
  getAll: (page = 0, size = 10) => api.get(`/tips?page=${page}&size=${size}`),
  
  // 获取单个提示
  getById: (id) => api.get(`/tips/${id}`),
  
  // 获取商家提示
  getByBusinessId: (businessId, page = 0, size = 10) => 
    api.get(`/tips/business/${businessId}?page=${page}&size=${size}`),
  
  // 创建提示
  create: (data) => api.post('/tips', data),
  
  // 更新提示
  update: (id, data) => api.put(`/tips/${id}`, data),
  
  // 删除提示
  delete: (id) => api.delete(`/tips/${id}`)
}

export default api
