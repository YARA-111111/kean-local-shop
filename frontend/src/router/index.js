import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import BusinessList from '../views/BusinessList.vue'
import BusinessDetail from '../views/BusinessDetail.vue'
import ReviewList from '../views/ReviewList.vue'
import TipList from '../views/TipList.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/businesses',
    name: 'BusinessList',
    component: BusinessList
  },
  {
    path: '/businesses/:id',
    name: 'BusinessDetail',
    component: BusinessDetail
  },
  {
    path: '/reviews',
    name: 'ReviewList',
    component: ReviewList
  },
  {
    path: '/tips',
    name: 'TipList',
    component: TipList
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
