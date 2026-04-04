// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------

import {merchantShippingRefundAuditApi} from "@/api/member";

export const refundSearchFormItems = [
  {
    type: 'user',
    label: '用户搜索：',
  },
  {
    type: 'input',
    prop: 'refundOrderNo',
    label: '退款单号：',
    placeholder: '请输入退款单号',
    class: 'form_content_width',
    clearable: true,
  },
  {
    type: 'date',
    prop: 'dateLimitAttr',
    label: '申请时间：',
    class: 'form_content_width',
    placeholder: '请选择',
    dateType: 'daterange',
    clearable: true,
  },
  // {
  //   type: 'select',
  //   prop: 'refundStatus',
  //   label: '退款方式：',
  //   class: 'selWidth',
  //   options: [
  //     { label: '全部', value: 99 },
  //     { label: '待审核', value: 0 },
  //     { label: '商家拒绝', value: 1 },
  //     { label: '退款中', value: 2 },
  //     { label: '已退款', value: 3 },
  //   ],
  //   clearable: true,
  // },
];

export const refundTableColumns = [
  {
    prop: 'refundOrderNo',
    minWidth: 180,
    label: '退款单号',
  },
  {
    prop: 'orderNo',
    minWidth: 190,
    label: '充值单号',
  },
  {
    label: '用户信息',
    minWidth: 180,
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
    prop: 'refundAmount',
    minWidth: 120,
    label: '退款金额(元)',
  },
  // {
  //   label: '退款后余额(元)',
  //   minWidth: 180,
  //   slotName: 'nickname' // 使用插槽自定义显示（包含注销状态）
  // },
  {
    prop: 'refundStatus',
    label: '退款状态',
    minWidth: 120,
    formatter: (row) => {
      const registerTypeMap = {
        0: '待审核',
        1: '商家拒绝',
        2: '退款中 ',
        3: '已退款',
        4: '用户撤销',
      };
      return registerTypeMap[row.refundStatus] || '--';
    }
  },
  {
    prop: 'createTime',
    minWidth: 150,
    label: '申请时间',
  },
  {
    // 操作列
    label: '操作',
    width: 130,
    fixed: 'right',
    slotName: 'operate' // 使用插槽自定义操作按钮
  }
];

export const refundTabs = [
  {
    name: '99',
    label: '全部',
  },
  {
    name: '0',
    label: '待审核',
  },
  {
    name: '2',
    label: '退款中',
  },
  {
    name: '3',
    label: '退款成功',
  },
  {
    name: '1',
    label: '已拒绝',
  },
];

//审核同意
export function onApprovedReview(refundOrderNo, cb) {
  this.$modalSure(`您确定要通过此退款审核吗？` ).then(() => {
    merchantShippingRefundAuditApi({
      auditType: 'success',
      refundOrderNo: refundOrderNo,
    }).then(() => {
      this.$message.success('审核成功')
      this.getSuccessful();
      cb && cb();
    });
  });
}

//审核拒绝
export function onOrderRefuse(refundOrderNo, cb) {
  this.$modalPrompt('textarea', '拒绝退款', null, '拒绝退款原因').then((V) => {
    merchantShippingRefundAuditApi({
      auditType: 'refuse',
      reason: V,
      refundOrderNo: refundOrderNo,
    }).then(() => {
      this.$message.success('审核成功');
      this.getSuccessful();
      cb && cb();
    });
  });
}