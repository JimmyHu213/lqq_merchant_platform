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
 * 预约工单分页列表
 */
export function employeeWorkOrderListApi(data) {
    return request.get(`employee/worker/order/list`,data);
}

/**
 * 工单详情
 */
export function employeeWorkOrderDetailApi(workOrderNo) {
    return request.get(`employee/worker/order/detail/${workOrderNo}`);
}

/**
 * 商户备注工单
 */
export function employeeWorkOrderMarkApi(data) {
    return request.post(`employee/worker/order/mark`, data);
}

/**
 * 商户备注工单
 */
export function employeeWorkRescheduleApi(data) {
    return request.post(`employee/worker/order/updateAgreement`, data);
}

/**
 * 服务人员列表
 */
export function staffListApi(data) {
    return request.get(`employee/service/staff/list`, data);
}

/**
 * 商户派单
 */
export function workerOrderAssignApi(data) {
    return request.post(`employee/worker/order/assign`, data);
}

/**
 * 商户改派
 */
export function workerOrderReassignApi(data) {
    return request.post(`employee/worker/order/reassign`, data);
}

/**
 * 商户强制完成工单
 */
export function workerOrderForceFinishApi(data) {
    return request.post(`employee/worker/order/forceFinish`, data);
}