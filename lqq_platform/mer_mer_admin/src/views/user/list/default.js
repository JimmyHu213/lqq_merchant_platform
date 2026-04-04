// 在用户列表组件的data中定义
import {couponCategoryFilter, couponUserTypeFilter} from "@/filters";

export const tableColumns = [
    {
        label: '用户信息',
        minWidth: 240,
        slotName: 'nickname'
    },
    {
        label: '手机号',
        minWidth: 130,
        prop: 'phone',
        formatter: (row) => {
            // 可以添加手机号格式化逻辑
            return row.phone || '--';
        }
    },
    // {
    //     label: '用户身份',
    //     minWidth: 180,
    //     prop: 'phone',
    //     formatter: (row) => {
    //         // 可以添加手机号格式化逻辑
    //         return row.phone || '--';
    //     }
    // },
    {
        label: '会员等级',
        minWidth: 150,
        prop: 'levelName',
    },
    {
        label: '购物金余额（元）',
        minWidth: 180,
        slotName: 'recharge',
    },
    {
        label: '购物金冻结',
        minWidth: 100,
        slotName: 'financialStatus',
    },
    {
        label: '入会时间',
        minWidth: 150,
        prop: 'membershipTime',
        formatter: (row) => {
            // 可以添加手机号格式化逻辑
            return row.membershipTime || '--';
        }
    },
    {
        // 操作列
        label: '操作',
        width: 130,
        fixed: 'right',
        slotName: 'operate' // 使用插槽自定义操作按钮
    }
];

export const shoppingCreditColumns = [
    {
        prop: 'title',
        label: '变动类型',
        minWidth: 120,
    },
    {
        label: '变动明细',
        minWidth: 180,
        slotName: 'recharge',
    },
    {
        label: '变动时间',
        minWidth: 150,
        prop: 'updateTime',
    }
];