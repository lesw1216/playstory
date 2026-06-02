import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'job-list',
      component: () => import('@/views/JobListView.vue'),
    },
  ],
})

export default router
