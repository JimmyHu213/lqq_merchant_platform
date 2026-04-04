package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.reservation.*;
import com.zbkj.common.response.reservation.*;

/**
 * 关断端工单服务类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/13
 */
public interface ManageWorkOrderService {

    /**
     * 获取工单分页列表
     *
     * @param request 预约工单列表请求对象
     * @return 工单分页列表
     */
    PageInfo<ReservationWorkOrderPageResponse> getWorkOrderListPage(WorkOrderSearchRequest request);

    /**
     * 获取商户端预约工单各状态数量
     */
    WorkOrderCountItemResponse getWorkOrderStatusNum(WorkOrderTabsHeaderRequest request);

    /**
     * 获取工单详情
     *
     * @param workOrderNo 工单号
     * @return 预约工单详情信息响应对象
     */
    ReservationWorkOrderDetailResponse getInfo(String workOrderNo);

    /**
     * 商户工单备注
     *
     * @param request 备注请求对象
     * @return 备注结果
     */
    Boolean merchantMark(WorkOrderRemarkRequest request);

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
     * 预约工单甘特图
     *
     * @param request 搜索条件
     * @return ReservationStatisticsResponse 预约工单统计响应对象
     */
    ReservationStatisticsResponse getOverView(ServiceStaffWorkOrderSearchRequest request);

    /**
     * 预约工单甘特图-数量统计
     *
     * @param request 搜索条件
     * @return WorkOrderCountViewResponse 预约工单-甘特图状态数量响应对象
     */
    WorkOrderCountViewResponse getViewNum(ServiceStaffWorkOrderSearchRequest request);
}
