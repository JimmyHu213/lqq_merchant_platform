package com.zbkj.front.service.impl.staff;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.constants.WorkOrderConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.merchant.MerchantInfo;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.reservation.MerchantServiceStaff;
import com.zbkj.common.model.reservation.ReservationWorkOrder;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.WorkOrderPageRequest;
import com.zbkj.common.request.WorkOrderRemarkRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.front.request.WorkOrderAwaitReceiveRequest;
import com.zbkj.front.request.WorkOrderClockInRequest;
import com.zbkj.front.request.WorkOrderServiceEvidenceRequest;
import com.zbkj.front.response.FrontWorkClockInPageInfoResponse;
import com.zbkj.front.response.FrontWorkOrderInfoResponse;
import com.zbkj.front.response.FrontWorkOrderPageDataResponse;
import com.zbkj.front.service.staff.StaffWorkOrderService;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务员工端-工单服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@Service
public class StaffWorkOrderServiceImpl implements StaffWorkOrderService {

    @Autowired
    private FrontTokenComponent tokenComponent;
    @Autowired
    private ReservationWorkOrderService reservationWorkOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private MerchantServiceStaffService merchantServiceStaffService;
    @Autowired
    private MerchantInfoService merchantInfoService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private SystemFormService systemFormService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderFlowRecordService orderFlowRecordService;


    /**
     * 待领取工单列表
     */
    @Override
    public PageInfo<FrontWorkOrderPageDataResponse> findAwaitReceivePage(WorkOrderAwaitReceiveRequest request) {
        Integer staffId = tokenComponent.getServiceStaffId();
        MerchantServiceStaff staff = merchantServiceStaffService.getById(staffId);
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(staff.getMerId());
        if (!merchantInfo.getVieSwitch()) {
            Page<FrontWorkOrderPageDataResponse> page = PageHelper.startPage(request.getPage(), request.getLimit());
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        String keywords = StrUtil.isNotBlank(request.getKeywords()) ? URLUtil.decode(request.getKeywords()) : "";
        PageInfo<ReservationWorkOrder> pageInfo = reservationWorkOrderService.findAwaitReceivePage(staff.getMerId(), keywords, request.getPage(), request.getLimit());
        List<ReservationWorkOrder> workOrderList = pageInfo.getList();
        if (CollUtil.isEmpty(workOrderList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<FrontWorkOrderPageDataResponse> responseList = workOrderList.stream().map(workOrder -> {
            OrderDetail orderDetail = orderDetailService.getById(workOrder.getOrderDetailId());
            FrontWorkOrderPageDataResponse dataResponse = new FrontWorkOrderPageDataResponse();
            dataResponse.setWorkOrderNo(workOrder.getWorkOrderNo());
            dataResponse.setReservationDate(workOrder.getReservationDate());
            dataResponse.setReservationTimeSlot(workOrder.getReservationTimeSlot());
            dataResponse.setUserName(workOrder.getUserName());
            dataResponse.setUserPhone(workOrder.getUserPhone());
            dataResponse.setUserAddress(workOrder.getUserAddress());
            dataResponse.setLongitude(workOrder.getLongitude());
            dataResponse.setLatitude(workOrder.getLatitude());
            dataResponse.setServiceStatus(workOrder.getServiceStatus());
            dataResponse.setRefundStatus(workOrder.getRefundStatus());
            dataResponse.setServiceType(workOrder.getServiceType());
            dataResponse.setServiceEvidenceFormId(workOrder.getServiceEvidenceFormId());
            dataResponse.setAllocateType(workOrder.getAllocateType());
            dataResponse.setProductId(orderDetail.getProductId());
            dataResponse.setProductName(orderDetail.getProductName());
            dataResponse.setImage(orderDetail.getImage());
            dataResponse.setSku(orderDetail.getSku());
            return dataResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 工单列表
     *
     * @param request 请求参数
     */
    @Override
    public PageInfo<FrontWorkOrderPageDataResponse> findPage(WorkOrderPageRequest request) {
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            CrmebDateUtil.validateDateLimitFormatDate(request.getDateLimit());
        }
        Integer staffId = tokenComponent.getServiceStaffId();
        request.setStaffId(staffId);
        PageInfo<ReservationWorkOrder> pageInfo = reservationWorkOrderService.findFrontPage(request);
        List<ReservationWorkOrder> workOrderList = pageInfo.getList();
        if (CollUtil.isEmpty(workOrderList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<FrontWorkOrderPageDataResponse> responseList = workOrderList.stream().map(workOrder -> {
            OrderDetail orderDetail = orderDetailService.getById(workOrder.getOrderDetailId());
            FrontWorkOrderPageDataResponse dataResponse = new FrontWorkOrderPageDataResponse();
            dataResponse.setWorkOrderNo(workOrder.getWorkOrderNo());
            dataResponse.setReservationDate(workOrder.getReservationDate());
            dataResponse.setReservationTimeSlot(workOrder.getReservationTimeSlot());
            dataResponse.setServiceType(workOrder.getServiceType());
            if (dataResponse.getServiceType().equals(WorkOrderConstants.SERVICE_TYPE_HOME_SERVICE)) {
                dataResponse.setUserName(workOrder.getUserName());
                dataResponse.setUserPhone(workOrder.getUserPhone());
                dataResponse.setUserAddress(workOrder.getUserAddress());
                dataResponse.setLongitude(workOrder.getLongitude());
                dataResponse.setLatitude(workOrder.getLatitude());
            }
            dataResponse.setServiceEvidenceFormId(workOrder.getServiceEvidenceFormId());
            dataResponse.setAllocateType(workOrder.getAllocateType());
            dataResponse.setServiceStatus(workOrder.getServiceStatus());
            dataResponse.setRefundStatus(workOrder.getRefundStatus());
            dataResponse.setProductId(orderDetail.getProductId());
            dataResponse.setProductName(orderDetail.getProductName());
            dataResponse.setImage(orderDetail.getImage());
            dataResponse.setSku(orderDetail.getSku());
            return dataResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 工单详情
     *
     * @param workOrderNo 工单号
     * @return 工单详情
     */
    @Override
    public FrontWorkOrderInfoResponse getInfo(String workOrderNo) {
        ReservationWorkOrder workOrder = reservationWorkOrderService.getByWorkOrderNo(workOrderNo);
        if (ObjectUtil.isNull(workOrder)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单不存在");
        }
        OrderDetail orderDetail = orderDetailService.getById(workOrder.getOrderDetailId());
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(orderDetail.getOrderNo());
        FrontWorkOrderInfoResponse response = new FrontWorkOrderInfoResponse();
        BeanUtils.copyProperties(workOrder, response);

        // 订单部分
        response.setProductName(orderDetail.getProductName());
        response.setImage(orderDetail.getImage());
        response.setSku(orderDetail.getSku());
        response.setUserRemark(merchantOrder.getUserRemark());

        // 服务人员信息 + 时长
        if (response.getServiceStaffId() > 0) {
            MerchantServiceStaff serviceStaff = merchantServiceStaffService.getById(response.getServiceStaffId());
            response.setServiceStaffName(serviceStaff.getName());
            response.setServiceStaffPhone(serviceStaff.getPhone());
            if (response.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_END)) {
                long serviceDuration = DateUtil.between(response.getServiceStartTime(), response.getServiceEndTime(), DateUnit.MINUTE, true);
                String duration = CrmebDateUtil.minutesToHoursMinutes(serviceDuration);
                response.setServiceDuration(duration);
            }
        }

        // 系统表单
        if (response.getServiceEvidenceFormId() > 0) {
            SystemForm systemForm = systemFormService.getById(response.getServiceEvidenceFormId());
            response.setServiceEvidenceFormValue(systemForm.getFormValue());
        }
        if (response.getReservationFormId() > 0) {
            SystemForm systemForm = systemFormService.getById(response.getReservationFormId());
            response.setReservationFormValue(systemForm.getFormValue());
        }
        return response;
    }

    /**
     * 领取工单
     *
     * @param workOrderNo 工单号
     */
    @Override
    public Boolean receiveWorkOrder(String workOrderNo) {
        Integer staffId = tokenComponent.getServiceStaffId();
        MerchantServiceStaff staff = merchantServiceStaffService.getById(staffId);
        ReservationWorkOrder workOrder = reservationWorkOrderService.getByWorkOrderNo(workOrderNo);
        if (ObjectUtil.isNull(workOrder) || !staff.getMerId().equals(workOrder.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单不存在");
        }
        if (workOrder.getServiceStatus() > WorkOrderConstants.SERVICE_STATUS_AWAIT) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单已分配，请换个工单领取");
        }
        if (workOrder.getRefundStatus().equals(WorkOrderConstants.REFUND_STATUS_REFUND)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单已退款，请换个工单领取");
        }
        Boolean receive = reservationWorkOrderService.receiveWorkOrder(staffId, workOrderNo);
        if (receive) {
            // 无需打卡工单，状态自动流转为服务中
            MerchantInfo merchantInfo = merchantInfoService.getByMerId(staff.getMerId());
            if (merchantInfo.getClockInType().equals(WorkOrderConstants.CLOCK_IN_STATUS_AUTO)) {
                asyncService.workOrderAutoClockIn(workOrderNo);
            }
        }
        return receive;
    }

    /**
     * 工单打卡
     */
    @Override
    public Boolean clockIn(WorkOrderClockInRequest request) {
        Integer staffId = tokenComponent.getServiceStaffId();
        MerchantServiceStaff staff = merchantServiceStaffService.getById(staffId);
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(staff.getMerId());
        ReservationWorkOrder workOrder = reservationWorkOrderService.getByWorkOrderNo(request.getWorkOrderNo());
        if (ObjectUtil.isNull(workOrder) || !workOrder.getServiceStaffId().equals(staffId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单不存在");
        }
        if (!workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_AWAIT_SERVICE)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单状态异常");
        }
        if (workOrder.getServiceType().equals(WorkOrderConstants.SERVICE_TYPE_HOME_SERVICE)) {
            if (merchantInfo.getClockInPhotoSwitch() && StrUtil.isBlank(request.getClockInPhoto())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请上传打卡照片");
            }
        }
        String remark = StrUtil.isNotBlank(request.getClockInRemark()) ? URLUtil.decode(request.getClockInRemark()) : "";
        return reservationWorkOrderService.clockIn(request.getWorkOrderNo(), request.getClockInPhoto(), remark);
    }

    /**
     * 服务过程留凭
     */
    @Override
    public Boolean serviceEvidence(WorkOrderServiceEvidenceRequest request) {
        Integer staffId = tokenComponent.getServiceStaffId();
        MerchantServiceStaff staff = merchantServiceStaffService.getById(staffId);
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(staff.getMerId());
        ReservationWorkOrder workOrder = reservationWorkOrderService.getByWorkOrderNo(request.getWorkOrderNo());
        if (ObjectUtil.isNull(workOrder) || !workOrder.getServiceStaffId().equals(staffId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单不存在");
        }
        if (!workOrder.getServiceStatus().equals(3)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单状态异常");
        }
        return reservationWorkOrderService.serviceEvidence(request.getWorkOrderNo(), request.getServiceEvidenceForm(), merchantInfo.getServiceEvidenceFormId());
    }

    /**
     * 工单服务结束
     */
    @Override
    public Boolean serviceEnd(String workOrderNo) {
        Integer staffId = tokenComponent.getServiceStaffId();
        MerchantServiceStaff staff = merchantServiceStaffService.getById(staffId);
        ReservationWorkOrder workOrder = reservationWorkOrderService.getByWorkOrderNo(workOrderNo);
        if (ObjectUtil.isNull(workOrder) || !workOrder.getServiceStaffId().equals(staffId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单不存在");
        }
        if (!workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_SERVICING)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单状态异常");
        }
        if (workOrder.getServiceType().equals(WorkOrderConstants.SERVICE_TYPE_HOME_SERVICE)) {
            MerchantInfo merchantInfo = merchantInfoService.getByMerId(staff.getMerId());
            if (merchantInfo.getServiceEvidenceSwitch() && merchantInfo.getServiceEvidenceFormId() > 0 && workOrder.getServiceEvidenceFormId().equals(0)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先完成服务过程留凭");
            }
        }
        Boolean result = reservationWorkOrderService.serviceEnd(workOrderNo);
        // 最后一个工单完成，订单状态流转
        if (result) {
            workOrderEndOrderFlow(workOrder.getOrderNo());
        }
        return result;
    }

    /**
     * 工单结束，订单流转
     * @param orderNo 订单编号
     */
    private void workOrderEndOrderFlow(String orderNo) {
        boolean isEnd = true;
        List<ReservationWorkOrder> workOrderList = reservationWorkOrderService.findByOrderNo(orderNo);
        for (ReservationWorkOrder workOrder : workOrderList) {
            if (workOrder.getRefundStatus().equals(WorkOrderConstants.REFUND_STATUS_REFUND)) {
                continue;
            }
            if (workOrder.getServiceStatus() < WorkOrderConstants.SERVICE_STATUS_END) {
                isEnd = false;
                break;
            }
        }
        if (!isEnd) return;
        Order order = orderService.getByOrderNo(orderNo);
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_TAKE_DELIVERY)) return;
        order.setStatus(OrderConstants.ORDER_STATUS_TAKE_DELIVERY);
        order.setReceivingTime(DateUtil.date());
        order.setUpdateTime(DateUtil.date());
        orderService.updateById(order);
        orderDetailService.takeDelivery(order.getOrderNo());
        orderFlowRecordService.orderReceipt(order.getOrderNo(), 0, "auto", null);
    }

    /**
     * 获取工单打卡页信息
     *
     * @param workOrderNo 工单号
     */
    @Override
    public FrontWorkClockInPageInfoResponse getClockInPageInfo(String workOrderNo) {
        Integer staffId = tokenComponent.getServiceStaffId();
        ReservationWorkOrder workOrder = reservationWorkOrderService.getByWorkOrderNo(workOrderNo);
        if (ObjectUtil.isNull(workOrder) || !workOrder.getServiceStaffId().equals(staffId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单不存在");
        }
        if (!workOrder.getServiceStatus().equals(2)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单状态异常");
        }
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(workOrder.getMerId());

        FrontWorkClockInPageInfoResponse response = new FrontWorkClockInPageInfoResponse();
        response.setWorkOrderNo(workOrderNo);
        response.setUserName(workOrder.getUserName());
        response.setUserPhone(workOrder.getUserPhone());
        response.setUserAddress(workOrder.getUserAddress());
        response.setLongitude(workOrder.getLongitude());
        response.setLatitude(workOrder.getLatitude());
        response.setClockInAddressSwitch(merchantInfo.getClockInAddressSwitch());
        response.setClockInDistance(merchantInfo.getClockInDistance());
        response.setClockInPhotoSwitch(merchantInfo.getClockInPhotoSwitch());
        return response;
    }

    /**
     * 工单备注
     */
    @Override
    public Boolean workRemark(WorkOrderRemarkRequest request) {
        Integer staffId = tokenComponent.getServiceStaffId();
        ReservationWorkOrder workOrder = reservationWorkOrderService.getByWorkOrderNo(request.getWorkOrderNo());
        if (ObjectUtil.isNull(workOrder) || !workOrder.getServiceStaffId().equals(staffId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单不存在");
        }
        return reservationWorkOrderService.workRemark(request.getWorkOrderNo(), URLUtil.decode(request.getRemark()));
    }
}
