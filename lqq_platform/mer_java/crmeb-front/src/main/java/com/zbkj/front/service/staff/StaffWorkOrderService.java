package com.zbkj.front.service.staff;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.WorkOrderPageRequest;
import com.zbkj.common.request.WorkOrderRemarkRequest;
import com.zbkj.front.request.WorkOrderAwaitReceiveRequest;
import com.zbkj.front.request.WorkOrderClockInRequest;
import com.zbkj.front.request.WorkOrderServiceEvidenceRequest;
import com.zbkj.front.response.FrontWorkClockInPageInfoResponse;
import com.zbkj.front.response.FrontWorkOrderInfoResponse;
import com.zbkj.front.response.FrontWorkOrderPageDataResponse;

/**
 * 服务员工端-工单服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
public interface StaffWorkOrderService {

    /**
     * 待领取工单列表
     */
    PageInfo<FrontWorkOrderPageDataResponse> findAwaitReceivePage(WorkOrderAwaitReceiveRequest request);

    /**
     * 工单列表
     *
     * @param request 请求参数
     */
    PageInfo<FrontWorkOrderPageDataResponse> findPage(WorkOrderPageRequest request);

    /**
     * 工单详情
     *
     * @param workOrderNo 工单号
     * @return 工单详情
     */
    FrontWorkOrderInfoResponse getInfo(String workOrderNo);

    /**
     * 领取工单
     * @param workOrderNo 工单号
     */
    Boolean receiveWorkOrder(String workOrderNo);

    /**
     * 工单打卡
     */
    Boolean clockIn(WorkOrderClockInRequest request);

    /**
     * 服务过程留凭
     */
    Boolean serviceEvidence(WorkOrderServiceEvidenceRequest request);

    /**
     * 工单服务结束
     */
    Boolean serviceEnd(String workOrderNo);

    /**
     * 获取工单打卡页信息
     *
     * @param workOrderNo 工单号
     */
    FrontWorkClockInPageInfoResponse getClockInPageInfo(String workOrderNo);

    /**
     * 工单备注
     */
    Boolean workRemark(WorkOrderRemarkRequest request);
}
