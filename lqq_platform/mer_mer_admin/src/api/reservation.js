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

/**
 * 编辑到店服务预约信息
 * @param pram
 */
export function reservationStoreEditApi(data) {
    return request({
        url: '/admin/merchant/reservation/config/store/edit',
        method: 'post',
        data
    });
}

/**
 * 到店服务详情
 */
export function reservationStoreInfoApi() {
    return request({
        url: '/admin/merchant/reservation/config/store/info',
        method: 'get'
    });
}

/**
 * 上门服务详情
 */
export function reservationHomeInfoApi() {
    return request({
        url: '/admin/merchant/reservation/config/home/info',
        method: 'get'
    });
}
/**
 * 编辑上门服务详情
 * @param pram
 */
export function reservationHomeEditApi(data) {
    return request({
        url: '/admin/merchant/reservation/config/home/edit',
        method: 'post',
        data
    });
}
/**
 * 预约工单甘特图
 * @param pram
 */
export function workOrderOverViewApi(params) {
    return request({
        url: '/admin/merchant/work/order/overView',
        method: 'get',
        params
    });
}

/**
 * 工单甘特图各状态数量
 * @param pram
 */
export function workOrderOverViewNumApi(params) {
    return request({
        url: '/admin/merchant/work/order/overView/num',
        method: 'get',
        params
    });
}

/**
 * 预约工单分页列表
 * @param pram
 */
export function workOrderListApi(params) {
    return request({
        url: '/admin/merchant/work/order/list',
        method: 'get',
        params
    });
}

/**
 * 获取工单各状态数量
 * @param pram
 */
export function workOrderStatusNumApi(params) {
    return request({
        url: '/admin/merchant/work/order/status/num',
        method: 'get',
        params
    });
}

/**
 * 工单详情
 * @param pram
 */
export function workOrderDetailApi(workOrderNo) {
    return request({
        url: `/admin/merchant/work/order/detail/${workOrderNo}`,
        method: 'get',
    });
}

/**
 * 商户派单
 * @param pram
 */
export function workOrderAssignApi(data) {
    return request({
        url: `/admin/merchant/work/order/assign`,
        method: 'post',
        data
    });
}

/**
 * 商户改派
 * @param pram
 */
export function workOrderReassignApi(data) {
    return request({
        url: `/admin/merchant/work/order/reassign`,
        method: 'post',
        data
    });
}

/**
 * 商户改约
 * @param pram
 */
export function workOrderUpdateAgreementApi(data) {
    return request({
        url: `/admin/merchant/work/order/updateAgreement`,
        method: 'post',
        data
    });
}

/**
 * 商户备注工单
 * @param pram
 */
export function workOrderMarkApi(data) {
    return request({
        url: `/admin/merchant/work/order/mark`,
        method: 'post',
        data
    });
}

/**
 * 商户强制完成工单
 * @param pram
 */
export function workOrderForceFinishApi(data) {
    return request({
        url: `/admin/merchant/work/order/forceFinish`,
        method: 'post',
        data
    });
}