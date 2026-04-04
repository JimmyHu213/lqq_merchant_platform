// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------

export const orderSearchFormItems = [
  {
    type: 'user',
    label: '用户搜索：',
  },
  {
    type: 'input',
    prop: 'orderNo',
    label: '充值单号：',
    placeholder: '请输入充值单号',
    class: 'form_content_width',
    clearable: true,
  },
  {
    type: 'date',
    prop: 'dateLimitAttr',
    label: '支付时间：',
    class: 'form_content_width',
    placeholder: '请选择',
    dateType: 'daterange',
    clearable: true,
  },
  {
    type: 'select',
    prop: 'payType',
    label: '支付方式：',
    placeholder: '请选择支付方式',
    class: 'selWidth',
    options: [
      { label: '微信', value: 'weixin' },
      { label: '支付宝', value: 'alipay' },
    ],
    clearable: true,
  },
];

export const orderTableColumns = [
  {
    prop: 'orderNo',
    minWidth: 190,
    label: '充值单号',
  },
  {
    label: '用户信息',
    minWidth: 200,
    slotName: 'nickname' // 使用插槽自定义显示（包含注销状态）
  },
  {
    // 手机号列
    label: '手机号码',
    minWidth: 120,
    prop: 'phone',
    formatter: (row) => {
      // 可以添加手机号格式化逻辑
      return row.phone || '--';
    }
  },
  {
    prop: 'rechargeAmount',
    minWidth: 120,
    label: '实付金额(元)',
  },
  {
    label: '充值金额(元)',
    minWidth: 130,
    slotName: 'amount' // 使用插槽自定义显示（包含注销状态）
  },
  {
    prop: 'payType',
    label: '支付方式',
    minWidth: 100,
    formatter: (row) => {
      const registerTypeMap = {
        'weixin': '微信',
        'alipay': '支付宝'
      };
      return registerTypeMap[row.payType] || '--';
    }
  },
  {
    prop: 'payTime',
    minWidth: 150,
    label: '支付时间',
  }
];