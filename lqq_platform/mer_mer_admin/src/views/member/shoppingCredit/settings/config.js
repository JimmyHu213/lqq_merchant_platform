// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------

export const settingsTableColumns = [
  {
    prop: 'id',
    width: 120,
    label: 'ID',
  },
  {
    prop: 'rechargeAmount',
    minWidth: 120,
    label: '充值金额(元)',
  },
  {
    prop: 'giftAmount',
    minWidth: 120,
    label: '赠送金额(元)',
  },
  {
    prop: 'sort',
    minWidth: 120,
    label: '排序',
  },
  {
    label: '创建时间',
    minWidth: 180,
    prop: 'createTime' // 使用插槽自定义显示（包含注销状态）
  },
  {
    label: '商城显示',
    minWidth: 120,
    prop: 'showStatus',
    type: 'switch',
    activeValue: 1,
    inactiveValue: 0,
    fixed: 'right',
    permission: 'merchant:shipping:credits:package:show:update'
  },
  {
    // 操作列
    label: '操作',
    width: 90,
    fixed: 'right',
    slotName: 'operate' // 使用插槽自定义操作按钮
  }
];