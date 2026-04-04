// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------

import {couponCategoryFilter, couponUserTypeFilter} from "@/filters";

export const merRechargeColumns = [
  {
    prop: 'id',
    minWidth: 50,
    label: 'ID',
  },
  {
    label: '充值金额',
    minWidth: 150,
    prop: 'name'
  },
  {
    prop: 'category',
    label: '类别',
    minWidth: 100,
    formatter: (row) => {
      const couponCategoryFilter = {
        1: '店铺',
        2: '商品',
        3: '通用',
        4: '品类',
        5: '品牌',
        6: '跨店',
      };
      return couponCategoryFilter[row.category];
    }
  },
  {
    label: '面值',
    minWidth: 100,
    prop: 'money'
  },
  {
    label: '身份类型',
    minWidth: 100,
    formatter: (row) => {
      const couponUserTypeFilter = {
        0: '普通',
        1: '商户会员',
      };
      return couponUserTypeFilter[row.receiveType] || '--';
    }
  },
  {
    prop: 'receiveType',
    label: '领取方式',
    minWidth: 120,
    formatter: (row) => {
      const couponUserTypeFilter = {
        1: '手动领取',
        2: '赠送券',
      };
      return couponUserTypeFilter[row.receiveType] || '--';
    }
  },
  {
    label: '领取日期',
    minWidth: 180,
    formatter: (row) => {
      let time = ''
      if(row.receiveEndTime){
        time = row.receiveStartTime + ' - ' + row.receiveEndTime
      }else{
        time = '不限时'
      }
      return time || '--';
    }
  },
  {
    label: '使用时间',
    minWidth: 180,
    formatter: (row) => {
      let time = ''
      if(row.day){
        time = `${row.day}天`
      }else{
        time = row.useStartTime + ' - ' + row.useEndTime
      }
      return time || '--';
    }
  },
  {
    label: '发布数量',
    minWidth: 100,
    slotName: 'number'
  },
  {
    label: '是否开启',
    minWidth: 100,
    type: 'switch',
    prop: 'status',
    fixed: 'right',
    permission: 'merchant:coupon:update:status'
  },
  {
    // 操作列
    label: '操作',
    width: 150,
    fixed: 'right',
    slotName: 'operate' // 使用插槽自定义操作按钮
  }
]