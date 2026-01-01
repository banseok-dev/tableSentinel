import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { left: 0, top: 0 }
  },
  routes: [
    // 메인 대시보드 (Ecommerce 가져옮)
    {
      path: '/',
      name: 'Dashboard',
      component: () => import('../views/Dashboard/Dashboard.vue'),
      meta: {
        title: 'Dashboard',
      },
    },
    
    // 방화벽 규칙 테이블 (Basic Tables 가져옮)
    {
      path: '/rules',
      name: 'FirewallRules',
      component: () => import('../views/Tables/FirewallRules.vue'),
      meta: {
        title: 'Firewall Rules',
      },
    },

    // 설정 (Form Elements 가져옮)
    {
      path: '/settings',
      name: 'Settings',
      component: () => import('../views/Pages/Settings.vue'),
      meta: {
        title: 'Settings',
      },
    },
  ],
})

export default router

router.beforeEach((to, from, next) => {
  document.title = `Vue.js ${to.meta.title} | tableSentinel Firewall Dashboard`
  next()
})
