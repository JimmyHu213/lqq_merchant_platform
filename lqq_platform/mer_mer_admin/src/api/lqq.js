// [LQQ-迁移] 溜圈圈自定义功能API - 商户端
import request from '@/utils/request';

// ==================== 锁客管理 ====================

/**
 * 锁客用户列表
 */
export function lockCustomerListApi(params) {
  return request({
    url: '/admin/merchant/lock-customer/list',
    method: 'get',
    params,
  });
}

/**
 * 锁客统计
 */
export function lockCustomerCountApi() {
  return request({
    url: '/admin/merchant/lock-customer/count',
    method: 'get',
  });
}

// ==================== 抽奖管理 ====================

/**
 * 创建抽奖活动
 */
export function lotteryCreateApi(data) {
  return request({
    url: '/admin/merchant/lottery/activity/create',
    method: 'post',
    data,
  });
}

/**
 * 编辑抽奖活动
 */
export function lotteryUpdateApi(data) {
  return request({
    url: '/admin/merchant/lottery/activity/update',
    method: 'post',
    data,
  });
}

/**
 * 抽奖活动列表
 */
export function lotteryListApi(params) {
  return request({
    url: '/admin/merchant/lottery/activity/list',
    method: 'get',
    params,
  });
}

/**
 * 抽奖活动详情
 */
export function lotteryDetailApi(id) {
  return request({
    url: `/admin/merchant/lottery/activity/detail/${id}`,
    method: 'get',
  });
}

/**
 * 开启/关闭活动
 */
export function lotterySwitchApi(id) {
  return request({
    url: `/admin/merchant/lottery/activity/switch/${id}`,
    method: 'post',
  });
}

/**
 * 删除活动
 */
export function lotteryDeleteApi(id) {
  return request({
    url: `/admin/merchant/lottery/activity/delete/${id}`,
    method: 'post',
  });
}

/**
 * 参与者列表
 */
export function lotteryParticipantsApi(activityId, params) {
  return request({
    url: `/admin/merchant/lottery/participants/${activityId}`,
    method: 'get',
    params,
  });
}

/**
 * 中奖记录列表
 */
export function lotteryWinnerListApi(params) {
  return request({
    url: '/admin/merchant/lottery/winner/list',
    method: 'get',
    params,
  });
}

/**
 * 核销兑奖
 */
export function lotteryRedeemApi(recordId) {
  return request({
    url: `/admin/merchant/lottery/redeem/${recordId}`,
    method: 'post',
  });
}

// ==================== 推广员管理 ====================

/**
 * 邀请推广员代理
 */
export function promoterInviteApi(data) {
  return request({
    url: '/admin/merchant/promoter/invite',
    method: 'post',
    data,
  });
}

/**
 * 解除代理
 */
export function promoterDismissApi() {
  return request({
    url: '/admin/merchant/promoter/dismiss',
    method: 'post',
  });
}

/**
 * 我的代理推广员信息
 */
export function promoterInfoApi() {
  return request({
    url: '/admin/merchant/promoter/info',
    method: 'get',
  });
}

/**
 * 修改代理佣金比例
 */
export function promoterUpdateRateApi(commissionRate) {
  return request({
    url: '/admin/merchant/promoter/update/rate',
    method: 'post',
    params: { commissionRate },
  });
}

// ==================== 分账记录 ====================

/**
 * 本商户分账记录列表
 * @param {Object} params { date }
 */
export function profitSharingListApi(params) {
  return request({
    url: '/admin/merchant/profit-sharing/list',
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
    url: `/admin/merchant/profit-sharing/detail/${orderNo}`,
    method: 'get',
  });
}
