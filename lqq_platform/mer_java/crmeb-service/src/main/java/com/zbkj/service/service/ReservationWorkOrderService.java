package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.reservation.ReservationWorkOrder;
import com.zbkj.common.request.WorkOrderPageRequest;
import com.zbkj.common.request.reservation.*;
import com.zbkj.common.response.reservation.ReservationWorkOrderDetailResponse;
import com.zbkj.common.response.reservation.ReservationWorkOrderPageResponse;
import com.zbkj.common.response.reservation.WorkOrderCountItemResponse;

import java.util.List;
import java.util.Map;

/**
 * @author HZW
 * @description ReservationWorkOrderService 接口
 * @date 2025-07-08
 */
public interface ReservationWorkOrderService extends IService<ReservationWorkOrder> {

    /**
     * 根据订单详情ID获取工单列表
     *
     * @param orderDetailId 订单详情ID
     */
    List<ReservationWorkOrder> findByOrderDetailId(Integer orderDetailId);

    /**
     * 获取待服务工单数量
     *
     * @param staffId 员工ID
     * @return 待服务工单数量
     */
    Integer getAwaitCountByStaffId(Integer staffId);

    /**
     * 获取服务中工单数量
     *
     * @param staffId 员工ID
     * @return 待服务工单数量
     */
    Integer getWorkingCountByStaffId(Integer staffId);

    /**
     * 获取已服务工单数量
     *
     * @param staffId 员工ID
     * @return 已服务工单数量
     */
    Integer getWorkedCountByStaffId(Integer staffId);

    /**
     * 移动端工单分页列表
     */
    PageInfo<ReservationWorkOrder> findFrontPage(WorkOrderPageRequest request);

    ReservationWorkOrder getByWorkOrderNo(String workOrderNo);

    /**
     * 获取待领取工单列表
     */
    PageInfo<ReservationWorkOrder> findAwaitReceivePage(Integer merId, String keywords, Integer page, Integer limit);

    /**
     * 领取工单
     *
     * @param staffId     员工ID
     * @param workOrderNo 工单号
     */
    Boolean receiveWorkOrder(Integer staffId, String workOrderNo);

    /**
     * 自动打卡
     *
     * @param workOrderNo 工单号
     */
    void autoClockIn(String workOrderNo);

    /**
     * 打卡
     *
     * @param workOrderNo 工单号
     */
    Boolean clockIn(String workOrderNo, String clockInPhoto, String clockInRemark);

    /**
     * 服务过程留凭
     */
    Boolean serviceEvidence(String workOrderNo, String serviceEvidenceForm, Integer serviceEvidenceFormId);

    /**
     * 服务结束
     */
    Boolean serviceEnd(String workOrderNo);

    /**
     * 获取工单分页列表
     *
     * @param request 预约工单列表请求对象
     * @return 工单分页列表
     */
    PageInfo<ReservationWorkOrderPageResponse> getWorkOrderListPage(WorkOrderSearchRequest request, SystemAdmin systemAdmin);

    /**
     * 获取商户端预约工单各状态数量
     */
    WorkOrderCountItemResponse getWorkOrderStatusNum(WorkOrderTabsHeaderRequest request, SystemAdmin systemAdmin);

    /**
     * 获取工单详情
     *
     * @param workOrderNo 工单号
     * @return 预约工单详情信息响应对象
     */
    ReservationWorkOrderDetailResponse getInfo(String workOrderNo, SystemAdmin systemAdmin);

    /**
     * 商户派单
     *
     * @param request 预约工单派单改派请求对象
     * @return 派单结果
     */
    Boolean assign(ReservationWorkOrderAssignRequest request, SystemAdmin systemAdmin);

    /**
     * 商户改派
     *
     * @param request 预约工单派单改派请求对象
     * @return 改派结果
     */
    Boolean reassign(ReservationWorkOrderAssignRequest request, SystemAdmin systemAdmin);

    /**
     * 商户改约
     *
     * @param request 改约请求对象
     * @return 改约结果
     */
    Boolean updateAgreement(UpdateAgreementRequest request, SystemAdmin systemAdmin);

    /**
     * 到店服务改约
     */
    Boolean updateAgreementByStore(String workOrderNo, String reservationDate, String reservationTimeSlot, String userName, String userPhone);

    /**
     * 上门服务改约
     */
    Boolean updateAgreementByHome(String workOrderNo, String reservationDate, String reservationTimeSlot, String userName, String userPhone, String userAddress, String longitude, String latitude);

    /**
     * 批量改约时间
     */
    Boolean batchUpdateAgreement(List<String> workOrderNoList, String reservationDate, String reservationTimeSlot);

    /**
     * 商户强制完结工单
     *
     * @param request 强制完结工单请求对象
     * @return 强制完结结果
     */
    Boolean forceFinish(ForceFinishWorkOrderRequest request, SystemAdmin systemAdmin);

    /**
     * 预约工单甘特图
     *
     * @param map 搜索条件
     * @return ReservationStatisticsResponse 预约工单统计响应对象
     */
    List<ReservationWorkOrder> getOverViewData(Map<String, Object> map);

    /**
     * 根据订单号获取工单列表
     *
     * @param orderNo 订单号
     */
    List<ReservationWorkOrder> findByOrderNo(String orderNo);

    /**
     * 获取未退款工单列表
     *
     * @param orderNo 订单号
     */
    List<ReservationWorkOrder> findNoRefundByOrderNo(String orderNo);

    /**
     * 工单批量退款
     *
     * @param workOrderIdList 工单ID列表
     */
    Boolean batchRefundByIdList(List<Integer> workOrderIdList);

    /**
     * 获取订单对应的工单数量
     *
     * @param orderNo 订单号
     * @return 工单数量
     */
    Integer getCountByOrderNo(String orderNo);

    /**
     * 是否存在工单
     * @param orderNo 订单号
     */
    Boolean isExistWorkByOrderNo(String orderNo);

    /**
     * 通过订单号获取工单ID列表
     * @param orderNo 订单号
     */
    List<Integer> findIdByOrderNo(String orderNo);

    /**
     * 商户工单备注
     *
     * @param request 备注请求对象
     * @return 备注结果
     */
    Boolean merchantMark(WorkOrderRemarkRequest request, SystemAdmin systemAdmin);
    /**
     * 工单备注
     * @param workOrderNo 工单号
     * @param remark 备注
     */
    Boolean workRemark(String workOrderNo, String remark);
}