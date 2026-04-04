import Layout from '@/layout';

export default {
  path: '/area',
  component: Layout,
  redirect: '/area/index',
  name: 'Area',
  meta: { title: '圈层管理', icon: 'clipboard' },
  children: [
    {
      path: 'index',
      component: () => import('@/views/area/index'),
      name: 'AreaIndex',
      meta: { title: '圈层列表' },
    },
    {
      path: 'create/:id?',
      component: () => import('@/views/area/create'),
      name: 'AreaCreate',
      meta: {
        title: '圈层维护',
        activeMenu: '/area/index',
        noCache: true
      },
      hidden: true,
    },
    {
      path: 'areamenu/index',
      component: () => import('@/views/area/areamenu/index'),
      name: 'AreaMenu',
      meta: { title: '圈层菜单管理' },
    },
     {
      path: 'areamenu/indexf',
      component: () => import('@/views/area/areamenu/indexf'),
      name: 'AreaMenuF',
      meta: { title: '圈层菜单管理' },
    },
    {
      path: 'areamenu/platformCircleRole',
      component: () => import('@/views/area/areamenu/platformCircleRole'),
      name: 'AreaMenu',
      meta: { title: '圈层超管管理' },
    },
  ]
};
