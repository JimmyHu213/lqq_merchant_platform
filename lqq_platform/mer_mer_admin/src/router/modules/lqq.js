// [LQQ-迁移] 溜圈圈功能路由 - 商户端
import Layout from '@/layout';

const lqqRouter = {
  path: '/lqq',
  component: Layout,
  redirect: '/lqq/lockCustomer/list',
  name: 'Lqq',
  meta: {
    title: '溜圈圈',
    icon: 'clipboard',
  },
  children: [
    {
      path: 'lockCustomer',
      component: () => import('@/views/lqq/lockCustomer/index'),
      name: 'LockCustomer',
      meta: { title: '锁客管理', icon: '' },
      children: [
        {
          path: 'list',
          component: () => import('@/views/lqq/lockCustomer/list'),
          name: 'LockCustomerList',
          meta: { title: '锁客用户', icon: '' },
        },
      ],
    },
    {
      path: 'lottery',
      component: () => import('@/views/lqq/lottery/index'),
      name: 'LqqLottery',
      meta: { title: '抽奖管理', icon: '' },
      children: [
        {
          path: 'list',
          component: () => import('@/views/lqq/lottery/activityList'),
          name: 'LotteryActivityList',
          meta: { title: '活动列表', icon: '' },
        },
        {
          path: 'create',
          component: () => import('@/views/lqq/lottery/activityForm'),
          name: 'LotteryCreate',
          meta: { title: '创建活动', icon: '', noCache: true, activeMenu: '/lqq/lottery/list' },
        },
        {
          path: 'edit/:id',
          component: () => import('@/views/lqq/lottery/activityForm'),
          name: 'LotteryEdit',
          meta: { title: '编辑活动', icon: '', noCache: true, activeMenu: '/lqq/lottery/list' },
        },
        {
          path: 'winners',
          component: () => import('@/views/lqq/lottery/winnerList'),
          name: 'LotteryWinners',
          meta: { title: '中奖记录', icon: '' },
        },
      ],
    },
    // [LQQ-迁移] 分账记录
    {
      path: 'profitSharing',
      component: () => import('@/views/lqq/profitSharing/index'),
      name: 'LqqProfitSharing',
      meta: { title: '分账管理', icon: '' },
      children: [
        {
          path: 'list',
          component: () => import('@/views/lqq/profitSharing/list'),
          name: 'ProfitSharingList',
          meta: { title: '分账记录', icon: '' },
        },
      ],
    },
    // [LQQ-迁移] 推广员管理
    {
      path: 'promoter',
      component: () => import('@/views/lqq/promoter/index'),
      name: 'LqqPromoter',
      meta: { title: '推广员管理', icon: '' },
      children: [
        {
          path: 'manage',
          component: () => import('@/views/lqq/promoter/manage'),
          name: 'PromoterManage',
          meta: { title: '我的推广员', icon: '' },
        },
      ],
    },
  ],
};

export default lqqRouter;
