// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import request from "@/utils/request.js";

// 获取商户会员等级列表
export function memberLevelListApi(merId){
    return request.get(`merchant/member/level/list/${merId}`,{},{ noAuth : true});
}

// 成为商户会员
export function memberBecomeApi(merId){
    return request.post(`merchant/member/become/${merId}`);
}

// 获取商户会员用户信息
export function memberUserInfoApi(merId){
    return request.get(`merchant/member/user/info/${merId}`);
}

// 获取购物金套餐列表
export function memberShoppingCreditsApi(merId){
    return request.get(`merchant/shopping/credits/package/list/${merId}`);
}

// 商户会员优惠券列表-根据id集合加载
export function getMemberCouponApi(ids) {
    return request.get(`coupon/merchant/member/list/ids`, {ids: ids}, {noAuth:true});
}

// 购物金套餐列表-根据id集合加载
export function getMemberCreditsApi(ids) {
    return request.get(`merchant/shopping/credits/package/list/ids`, {ids: ids}, {noAuth:true});
}

// 购买购物金套餐
export function shoppingCreditsPayApi(data) {
    return request.post(`merchant/shopping/credits/pay/package`, data);
}

// 购买购物金记录
export function shoppingCreditsRecordApi(data) {
    return request.post(`merchant/shopping/credits/pay/package/record`, data);
}

// 购物金流水记录
export function shoppingCreditsUserRecordApi(data) {
    return request.post(`merchant/shopping/credits/user/record/page`, data);
}

// 获取商户会员购物金资产
export function shoppingCreditsUserAssetApi(merId) {
    return request.get(`merchant/shopping/credits/get/merchant/member/asset/${merId}`);
}

// 购物金订单退款申请
export function shoppingCreditsRefundApplyApi(data) {
    return request.post(`merchant/shopping/credits/order/refund/apply`, data);
}

// 购物金退款订单分页列表
export function shoppingCreditsRefundOrderApi(data) {
    return request.get(`merchant/shopping/credits/refund/order/page`, data);
}

// 商户会员注销
export function memberLogoffApi(merId) {
    return request.post(`merchant/member/logoff/${merId}`);
}