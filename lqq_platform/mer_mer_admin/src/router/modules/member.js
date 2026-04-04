// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import Layout from '@/layout';

const userRouter = {
    path: '/member',
    component: Layout,
    redirect: '/member/overview',
    name: 'Member',
    meta: {
        title: '会员',
        icon: 'clipboard',
    },
    children: [
        {
            path: 'overview',
            component: () => import('@/views/member/overview'),
            name: 'Overview',
            meta: { title: '会员概览', icon: '' },
        },
        {
            path: 'user',
            component: () => import('@/views/member/user'),
            name: 'User',
            meta: { title: '用户列表', icon: '' },
        },
        {
            path: 'shoppingCredit',
            component: () => import('@/views/member/shoppingCredit'),
            name: 'ShoppingCredit',
            meta: { title: '购物金', icon: '' },
            children: [
                {
                    path: 'order',
                    component: () => import('@/views/member/shoppingCredit/order'),
                    name: 'CreditOrder',
                    meta: { title: '充值订单', icon: '' },
                },
                {
                    path: 'settings',
                    component: () => import('@/views/member/shoppingCredit/settings'),
                    name: 'Settings',
                    meta: { title: '充值套餐', icon: '' },
                },
                {
                    path: 'refund',
                    component: () => import('@/views/member/shoppingCredit/refund'),
                    name: 'Refund',
                    meta: { title: '充值退款', icon: '' },
                },
            ]
        },
        {
            path: 'level',
            component: () => import('@/views/member/level'),
            name: 'Level',
            meta: { title: '会员等级', icon: '' },
        },
        {
            path: 'benefit',
            component: () => import('@/views/member/benefit'),
            name: 'Benefit',
            meta: { title: '会员权益', icon: '' },
        },
        {
            path: 'agreement',
            component: () => import('@/views/member/agreement'),
            name: 'Agreement',
            meta: { title: '会员协议', icon: '' },
        },
    ],
};

export default userRouter;
