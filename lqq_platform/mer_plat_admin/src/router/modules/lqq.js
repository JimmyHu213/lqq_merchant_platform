// [LQQ-迁移] 溜圈圈功能路由 - 平台端
import Layout from '@/layout';

const lqqRouter = {
  path: '/lqq',
  component: Layout,
  redirect: '/lqq/lockCustomer',
  name: 'Lqq',
  meta: {
    title: '溜圈圈',
    icon: 'clipboard',
  },
  children: [
    {
      path: 'lockCustomer',
      component: () => import('@/views/lqq/lockCustomer/index'),
      name: 'PlatLockCustomer',
      meta: { title: '全局锁客记录', icon: '' },
    },
    {
      path: 'lottery',
      component: () => import('@/views/lqq/lottery/index'),
      name: 'PlatLottery',
      meta: { title: '抽奖管理', icon: '' },
      children: [
        {
          path: 'list',
          component: () => import('@/views/lqq/lottery/activityList'),
          name: 'PlatLotteryList',
          meta: { title: '活动审核', icon: '' },
        },
        {
          path: 'records',
          component: () => import('@/views/lqq/lottery/recordList'),
          name: 'PlatLotteryRecords',
          meta: { title: '抽奖记录', icon: '' },
        },
        {
          path: 'winners',
          component: () => import('@/views/lqq/lottery/winnerList'),
          name: 'PlatLotteryWinners',
          meta: { title: '中奖记录', icon: '' },
        },
      ],
    },
    // [LQQ-迁移] 分账管理
    {
      path: 'profitSharing',
      component: () => import('@/views/lqq/profitSharing/index'),
      name: 'PlatProfitSharing',
      meta: { title: '分账管理', icon: '' },
      children: [
        {
          path: 'list',
          component: () => import('@/views/lqq/profitSharing/list'),
          name: 'PlatProfitSharingList',
          meta: { title: '分账记录', icon: '' },
        },
        {
          path: 'config',
          component: () => import('@/views/lqq/profitSharing/config'),
          name: 'PlatProfitSharingConfig',
          meta: { title: '分账配置', icon: '' },
        },
      ],
    },
    // [LQQ-迁移] 推广员管理
    {
      path: 'promoter',
      component: () => import('@/views/lqq/promoter/index'),
      name: 'PlatPromoter',
      meta: { title: '推广员管理', icon: '' },
      children: [
        {
          path: 'list',
          component: () => import('@/views/lqq/promoter/list'),
          name: 'PlatPromoterList',
          meta: { title: '推广员审核', icon: '' },
        },
      ],
    },
  ],
};

export default lqqRouter;
