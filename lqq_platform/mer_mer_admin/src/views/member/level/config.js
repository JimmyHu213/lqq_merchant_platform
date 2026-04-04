// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------

export const levelTableColumns = [
  {
    prop: 'level',
    minWidth: 100,
    label: '等级编号',
    formatter: (row) => {
      return `LV${row.level}`;
    }
  },
  {
    label: '等级名称',
    minWidth: 150,
    prop: 'name'
  },
  {
    label: '升级条件',
    minWidth: 240,
    formatter: (row) => {
      // 可以添加手机号格式化逻辑
      return row.level === 1 ? '此等级为最低等级，领卡即可达到' : `消费累计金额达到${row.thresholdAmount}元`;
    }
  },
  {
    minWidth: 120,
    label: '等级权益',
    slotName: 'interests' // 使用插槽自定义显示（包含注销状态）
  },
  {
    label: '会员人数',
    minWidth: 180,
    prop: 'num'
  },
  {
    // 操作列
    label: '操作',
    width: 90,
    fixed: 'right',
    slotName: 'operate' // 使用插槽自定义操作按钮
  }
];

export const couponTableColumns = [
  {
    label: '优惠券名称',
    minWidth: 180,
    prop: 'name'
  },
  {
    label: '面值',
    minWidth: 180,
    prop: 'money'
  },
  {
    label: '使用门槛',
    minWidth: 180,
    formatter: (row) => {
      return row.minPrice === 0 ? '不限制' : row.minPrice;
    }
  },
  {
    label: '有效期',
    minWidth: 180,
    formatter: (row) => {
      return row.isFixedTime ? row.useStartTime + ' 一 ' + row.useEndTime :row.day + '天'
    }
  },
]