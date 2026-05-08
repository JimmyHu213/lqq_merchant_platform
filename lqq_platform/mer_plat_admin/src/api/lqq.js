// [LQQ-迁移] 溜圈圈自定义功能API - 平台端
import request from '@/utils/request';

// ==================== 锁客管理 ====================

/**
 * 全平台锁客记录
 */
export function lockCustomerListApi(params) {
  return request({
    url: '/admin/platform/lock-customer/list',
    method: 'get',
    params,
  });
}

// ==================== 抽奖管理 ====================

/**
 * 全平台抽奖活动列表
 */
export function lotteryListApi(params) {
  return request({
    url: '/admin/platform/lottery/activity/list',
    method: 'get',
    params,
  });
}

/**
 * 抽奖活动详情
 */
export function lotteryDetailApi(id) {
  return request({
    url: `/admin/platform/lottery/activity/detail/${id}`,
    method: 'get',
  });
}

/**
 * 审核活动
 */
export function lotteryAuditApi(data) {
  return request({
    url: '/admin/platform/lottery/activity/audit',
    method: 'post',
    data,
  });
}

/**
 * 强制关闭活动
 */
export function lotteryCloseApi(id) {
  return request({
    url: `/admin/platform/lottery/activity/close/${id}`,
    method: 'post',
  });
}

/**
 * 删除活动
 */
export function lotteryDeleteApi(id) {
  return request({
    url: `/admin/platform/lottery/activity/delete/${id}`,
    method: 'post',
  });
}

/**
 * 所有抽奖记录
 */
export function lotteryRecordListApi(params) {
  return request({
    url: '/admin/platform/lottery/record/list',
    method: 'get',
    params,
  });
}

/**
 * 所有中奖记录
 */
export function lotteryWinnerListApi(params) {
  return request({
    url: '/admin/platform/lottery/winner/list',
    method: 'get',
    params,
  });
}

// ==================== 推广员管理 ====================

/**
 * 所有推广员-商户绑定关系列表
 */
export function promoterMerchantListApi(params) {
  return request({
    url: '/admin/platform/promoter-merchant/list',
    method: 'get',
    params,
  });
}

/**
 * 审核推广员绑定申请
 */
export function promoterMerchantAuditApi(data) {
  return request({
    url: '/admin/platform/promoter-merchant/audit',
    method: 'post',
    data,
  });
}

/**
 * 强制解绑推广员
 */
export function promoterMerchantForceUnbindApi(id) {
  return request({
    url: `/admin/platform/promoter-merchant/force-unbind/${id}`,
    method: 'post',
  });
}

// ==================== 分账管理 ====================

/**
 * 全平台分账记录列表
 * @param {Object} params { merId, date }
 */
export function profitSharingListApi(params) {
  return request({
    url: '/admin/platform/profit-sharing/list',
    method: 'get',
    params,
  });
}

/**
 * 分账记录详情 — 按订单号查询
 * @param {string} orderNo 订单号
 */
export function profitSharingDetailApi(orderNo) {
  return request({
    url: `/admin/platform/profit-sharing/detail/${orderNo}`,
    method: 'get',
  });
}
