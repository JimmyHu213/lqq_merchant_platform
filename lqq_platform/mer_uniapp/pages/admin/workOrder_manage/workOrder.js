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

/**
 * 服务员工登录
 */
export function staffLoginApi() {
    return request.post(`staff/login/index`);
}

/**
 * 待领取工单分页列表
 */
export function workOrderAwaitListApi(data) {
    return request.get(`staff/work/order/await/receive/page`,data);
}

/**
 * 我的工单分页列表
 * @data
 */
export function workOrderListApi(data) {
    return request.get(`staff/work/order/page`, data);
}

/**
 * 商户预约设置信息
 */
export function reservationConfigApi() {
    return request.get(`staff/merchant/reservation/config`);
}

/**
 * 获取工单打卡页信息
 */
export function clockInfoApi(workOrderNo) {
    return request.get(`staff/work/order/clock/in/page/info/${workOrderNo}`);
}

/**
 * 工单打卡
 */
export function checkinStaffOrderApi(data) {
    return request.post(`staff/work/order/clock/in`, data);
}

/**
 * 通过坐标获取地址
 */
export function getCoordinateAddressApi(data) {
    return request.get(`staff/address/get/coordinate/address`, data);
}

/**
 * 工单详情
 */
export function getWorkOrderInfoApi(workOrderNo) {
    return request.get(`staff/work/order/info/${workOrderNo}`);
}

/**
 * 工单备注
 */
export function workOrderRemarkApi(data) {
    return request.post(`staff/work/order/remark`,data);
}

/**
 * 工单服务结束
 */
export function workOrderServiceEndApi(workOrderNo) {
    return request.post(`staff/work/order/service/end/${workOrderNo}`);
}

/**
 * 领取工单
 */
export function workOrderReceiveApi(workOrderNo) {
    return request.post(`staff/work/order/receive/${workOrderNo}`);
}

/**
 * 商户预约服务留凭表单信息
 */
export function serviceFormInfoApi() {
    return request.get(`staff/merchant/reservation/service/evidence/form/info`);
}

/**
 * 工单服务过程留凭
 */
export function serviceEvidenceApi(data) {
    return request.post(`staff/work/order/service/evidence`, data);
}

/**
 * 获取登录员工信息
 */
export function staffInfoApi() {
    return request.get(`staff/login/staff/info`);
}