package com.zbkj.front.service.employee;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.reservation.*;
import com.zbkj.common.response.reservation.ReservationWorkOrderDetailResponse;
import com.zbkj.common.response.reservation.ReservationWorkOrderPageResponse;
import com.zbkj.common.response.reservation.WorkOrderCountItemResponse;

/**
 *  EmployeeWorkerOrderService 接口
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface EmployeeWorkOrderService {

    /**
     * 获取工单分页列表
     *
     * @param request 预约工单列表请求对象
     * @return 工单分页列表
     */
    PageInfo<ReservationWorkOrderPageResponse> getWorkOrderListPage(WorkOrderSearchRequest request);

    /**
     * 获取工单详情
     *
     * @param workOrderNo 工单号
     * @return 预约工单详情信息响应对象
     */
    ReservationWorkOrderDetailResponse getInfo(String workOrderNo);

    /**
     * 商户派单
     *
     * @param request 预约工单派单改派请求对象
     * @return 派单结果
     */
    Boolean assign(ReservationWorkOrderAssignRequest request);

    /**
     * 商户改派
     *
     * @param request 预约工单派单改派请求对象
     * @return 改派结果
     */
    Boolean reassign(ReservationWorkOrderAssignRequest request);

    /**
     * 商户改约
     *
     * @param request 改约请求对象
     * @return 改约结果
     */
    Boolean updateAgreement(UpdateAgreementRequest request);


    /**
     * 商户强制完结工单
     *
     * @param request 强制完结工单请求对象
     * @return 强制完结结果
     */
    Boolean forceFinish(ForceFinishWorkOrderRequest request);


    /**
     * 获取商户端预约工单各状态数量
     */
    WorkOrderCountItemResponse getWorkOrderStatusNum(WorkOrderTabsHeaderRequest request);

    /**
     * 商户备注工单
     *
     * @param request 工单备注请求对象
     */
    Boolean merchantMark(WorkOrderRemarkRequest request);
}
