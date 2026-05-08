// [LQQ-迁移] 溜圈圈自定义功能API
import request from "@/utils/request.js";

// ==================== 抽奖相关 ====================

/**
 * 抽奖活动列表
 */
export function lotteryListApi(data) {
  return request.get('lottery/list', data, { noAuth: true });
}

/**
 * 抽奖活动详情
 */
export function lotteryDetailApi(id) {
  return request.get(`lottery/detail/${id}`, {}, { noAuth: true });
}

/**
 * 参与抽奖
 */
export function lotteryParticipateApi(activityId) {
  return request.post('lottery/participate', { activityId });
}

/**
 * 我的抽奖记录
 */
export function lotteryMyRecordsApi(data) {
  return request.get('lottery/my/records', data);
}

/**
 * 当前期参与者列表
 */
export function lotteryParticipantsApi(activityId, data) {
  return request.get(`lottery/participants/${activityId}`, data, { noAuth: true });
}

// ==================== 优惠券转赠 ====================

/**
 * 优惠券转赠
 * @param {number} couponUserId 用户优惠券ID
 * @param {number} recipientUid 受赠者用户ID
 */
export function couponTransferApi(couponUserId, recipientUid) {
  return request.post('coupon/transfer', { couponUserId, recipientUid });
}

// ==================== 附近商铺(LBS) ====================

/**
 * 附近商铺搜索（带经纬度距离排序）
 * @param {Object} data { latitude, longitude, distance, keywords, page, limit }
 */
export function nearbyMerchantSearchApi(data) {
  return request.get('merchant/search/list', data, { noAuth: true });
}

// ==================== 推广员 ====================

/**
 * 我代理的商户列表
 */
export function promoterMyMerchantsApi() {
  return request.get('promoter/my/merchants');
}

/**
 * 推广员佣金统计
 */
export function promoterCommissionStatsApi() {
  return request.get('promoter/my/commission/stats');
}

// ==================== 佣金明细 ====================

/**
 * 我的佣金明细列表（分账记录中属于当前用户的记录）
 * @param {Object} data { page, limit }
 */
export function promoterCommissionDetailApi(data) {
  return request.get('promoter/my/commission/detail', data);
}

// ==================== 锁客 ====================

/**
 * 获取用户锁定的商铺信息（通过用户信息中 lockedMerchantId 获取商铺详情）
 */
export function getLockedMerchantApi(id) {
  return request.get(`merchant/detail/${id}`, {}, { noAuth: true });
}
