// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import request from '@/utils/request';

// 会员权益列表
export function memberBenefitsListApi() {
    return request({
        url: '/admin/merchant/member/benefits/list',
        method: 'get',
    });
}

// 会员权益保存
export function memberBenefitsSaveApi(data) {
    return request({
        url: '/admin/merchant/member/benefits/save',
        method: 'post',
        data
    });
}

// 会员等级列表
export function memberLevelListApi() {
    return request({
        url: '/admin/merchant/member/level/list',
        method: 'get',
    });
}

// 会员等级新增
export function memberLevelAddApi(data) {
    return request({
        url: '/admin/merchant/member/level/add',
        method: 'post',
        data
    });
}

// 会员等级编辑
export function memberLevelUpdateApi(data) {
    return request({
        url: '/admin/merchant/member/level/update',
        method: 'post',
        data
    });
}

// 会员等级删除
export function memberLevelDeleteApi(id) {
    return request({
        url: `/admin/merchant/member/level/delete/${id}`,
        method: 'post'
    });
}

// 购物金套餐添加
export function creditsAddApi(data) {
    return request({
        url: `/admin/merchant/shipping/credits/package/add`,
        method: 'post',
        data
    });
}

// 购物金套餐删除
export function creditsDeleteApi(id) {
    return request({
        url: `/admin/merchant/shipping/credits/package/delete/${id}`,
        method: 'post'
    });
}

// 购物金套餐列表
export function creditsListApi() {
    return request({
        url: `/admin/merchant/shipping/credits/package/list`,
        method: 'get'
    });
}

// 购物金套餐编辑
export function creditsUpdateApi(data) {
    return request({
        url: `/admin/merchant/shipping/credits/package/update`,
        method: 'post',
        data
    });
}

// 购物金套餐显示状态变更
export function creditsUpdateShowApi(id) {
    return request({
        url: `/admin/merchant/shipping/credits/package/update/show/${id}`,
        method: 'post'
    });
}

// 会员等级分布统计
export function memberLevelDistributionApi() {
    return request({
        url: '/admin/merchant/member/level/distribution',
        method: 'get'
    });
}

// 购物金订单分页列表
export function shippingOrderApi(params) {
    let data = {
        ...params,
    }
    delete data.dateLimitAttr
    return request({
        url: '/admin/merchant/shipping/credits/order/page',
        method: 'get',
        params: data
    });
}

// 折线趋势图统计
export function orderStatisticsChartApi() {
    return request({
        url: '/admin/merchant/order/statistics/chart',
        method: 'get'
    });
}

// 订单发货统计
export function orderStatisticsShippingApi() {
    return request({
        url: '/admin/merchant/order/statistics/shipping',
        method: 'get'
    });
}

// 顶部核心数据统计
export function orderStatisticsTopApi() {
    return request({
        url: '/admin/merchant/order/statistics/top',
        method: 'get'
    });
}

// 订单类型统计
export function orderStatisticsTypeApi() {
    return request({
        url: '/admin/merchant/order/statistics/type',
        method: 'get'
    });
}

// 购物金退款订单分页列表
export function shippingCreditsRefundListApi(params) {
    let data = {
        ...params,
    }
    delete data.dateLimitAttr
    return request({
        url: '/admin/merchant/shipping/credits/refund/order/page',
        method: 'get',
        params: data
    });
}

// 商户会员开卡协议详情
export function merchantAgreementInfoApi() {
    return request({
        url: '/admin/merchant/agreement/member/card/open/info',
        method: 'get'
    });
}

// 商户会员开卡协议保存
export function merchantAgreementSaveApi(data) {
    return request({
        url: '/admin/merchant/agreement/member/card/open/save',
        method: 'post',
        data
    });
}

// 购物金退款订单详情
export function merchantShippingRefundInfoApi(refundOrderNo) {
    return request({
        url: `admin/merchant/shipping/credits/refund/order/info/${refundOrderNo}`,
        method: 'get'
    });
}

// 购物金退款订单详情
export function merchantShippingRefundAuditApi(data) {
    return request({
        url: `admin/merchant/shipping/credits/refund/order/audit`,
        method: 'post',
        data
    });
}

// 获取会员环比数据
export function getMemberStatisticsMomDataApi(params) {
    return request({
        url: `admin/merchant/member/statistics/mom/data`,
        method: 'get',
        params
    });
}

// 获取会员概览数据
export function getMemberOverviewDataApi() {
    return request({
        url: `admin/merchant/member/statistics/overview`,
        method: 'get'
    });
}

// 获取会员新增趋势数据
export function getMemberNewTrendDataApi(params) {
    return request({
        url: `admin/merchant/member/statistics/new/trend/data`,
        method: 'get',
        params
    });
}

// 获取会员消费趋势数据
export function getMemberConsumeTrendDataApi(params) {
    return request({
        url: `admin/merchant/member/statistics/consume/trend/data`,
        method: 'get',
        params
    });
}

// 商户会员可用优惠券列表
export function couponUsableApi() {
    return request({
        url: `admin/merchant/coupon/member/usable/list`,
        method: 'get'
    });
}