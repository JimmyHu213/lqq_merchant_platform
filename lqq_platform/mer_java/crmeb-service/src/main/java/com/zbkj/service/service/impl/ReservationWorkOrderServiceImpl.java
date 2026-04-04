package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.constants.WorkOrderConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantInfo;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.reservation.MerchantServiceStaff;
import com.zbkj.common.model.reservation.ReservationWorkOrder;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.WorkOrderPageRequest;
import com.zbkj.common.request.reservation.*;
import com.zbkj.common.response.reservation.ReservationWorkOrderDetailResponse;
import com.zbkj.common.response.reservation.ReservationWorkOrderPageResponse;
import com.zbkj.common.response.reservation.WorkOrderCountItemResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.reservation.ReservationWorkOrderDao;
import com.zbkj.service.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HZW
 * @description ReservationWorkOrderServiceImpl 接口实现
 * @date 2025-07-08
 */
@Service
public class ReservationWorkOrderServiceImpl extends ServiceImpl<ReservationWorkOrderDao, ReservationWorkOrder> implements ReservationWorkOrderService {

    @Resource
    private ReservationWorkOrderDao dao;
    @Autowired
    private MerchantServiceStaffService merchantServiceStaffService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private MerchantInfoService merchantInfoService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemFormService systemFormService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CrmebConfig crmebConfig;



    /**
     * 根据订单详情ID获取工单列表
     *
     * @param orderDetailId 订单详情ID
     */
    @Override
    public List<ReservationWorkOrder> findByOrderDetailId(Integer orderDetailId) {
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ReservationWorkOrder::getOrderDetailId, orderDetailId);
        return dao.selectList(lqw);
    }

    /**
     * 获取待服务工单数量
     *
     * @param staffId 员工ID
     * @return 待服务工单数量
     */
    @Override
    public Integer getAwaitCountByStaffId(Integer staffId) {
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ReservationWorkOrder::getServiceStaffId, staffId);
        lqw.eq(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_AWAIT_SERVICE);
        lqw.lt(ReservationWorkOrder::getRefundStatus, WorkOrderConstants.REFUND_STATUS_REFUND);
        return dao.selectCount(lqw);
    }

    /**
     * 获取服务中工单数量
     *
     * @param staffId 员工ID
     * @return 待服务工单数量
     */
    @Override
    public Integer getWorkingCountByStaffId(Integer staffId) {
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ReservationWorkOrder::getServiceStaffId, staffId);
        lqw.eq(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_SERVICING);
        lqw.lt(ReservationWorkOrder::getRefundStatus, WorkOrderConstants.REFUND_STATUS_REFUND);
        return dao.selectCount(lqw);
    }

    /**
     * 获取已服务工单数量
     *
     * @param staffId 员工ID
     * @return 已服务工单数量
     */
    @Override
    public Integer getWorkedCountByStaffId(Integer staffId) {
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ReservationWorkOrder::getServiceStaffId, staffId);
        lqw.eq(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_END);
        lqw.lt(ReservationWorkOrder::getRefundStatus, WorkOrderConstants.REFUND_STATUS_REFUND);
        return dao.selectCount(lqw);
    }

    /**
     * 移动端工单分页列表
     */
    @Override
    public PageInfo<ReservationWorkOrder> findFrontPage(WorkOrderPageRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", request.getStatus());
        map.put("staffId", request.getStaffId());
        if (StrUtil.isNotBlank(request.getKeywords())) {
            map.put("keywords", URLUtil.decode(request.getKeywords()));
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            List<String> dateStrList = CrmebUtil.stringToArrayStr(request.getDateLimit());
            String startDate = dateStrList.get(0);
            String endDate = dateStrList.get(1);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
        }
        Page<ReservationWorkOrder> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<ReservationWorkOrder> workOrderList = dao.findFrontList(map);
        return CommonPage.copyPageInfo(page, workOrderList);
    }

    @Override
    public ReservationWorkOrder getByWorkOrderNo(String workOrderNo) {
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ReservationWorkOrder::getWorkOrderNo, workOrderNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取待领取工单列表
     */
    @Override
    public PageInfo<ReservationWorkOrder> findAwaitReceivePage(Integer merId, String keywords, Integer page, Integer limit) {
        Page<ReservationWorkOrder> orderPage = PageHelper.startPage(page, limit);
        List<ReservationWorkOrder> workOrderList = dao.findAwaitReceiveList(merId, keywords);
        return CommonPage.copyPageInfo(orderPage, workOrderList);
    }

    /**
     * 领取工单
     *
     * @param staffId     员工ID
     * @param workOrderNo 工单号
     */
    @Override
    public Boolean receiveWorkOrder(Integer staffId, String workOrderNo) {
        LambdaUpdateWrapper<ReservationWorkOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_AWAIT_SERVICE);
        wrapper.set(ReservationWorkOrder::getServiceStaffId, staffId);
        wrapper.set(ReservationWorkOrder::getAllocateType, WorkOrderConstants.ALLOCATE_TYPE_WREST);
        wrapper.eq(ReservationWorkOrder::getWorkOrderNo, workOrderNo);
        wrapper.eq(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_AWAIT);
        return update(wrapper);
    }

    /**
     * 自动打卡
     *
     * @param workOrderNo 工单号
     */
    @Override
    public void autoClockIn(String workOrderNo) {
        ReservationWorkOrder workOrder = getByWorkOrderNo(workOrderNo);
        if (!workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_AWAIT_SERVICE)) {
            return;
        }
        LambdaUpdateWrapper<ReservationWorkOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_SERVICING);
        wrapper.set(ReservationWorkOrder::getClockInStatus, WorkOrderConstants.CLOCK_IN_STATUS_AUTO);
        wrapper.set(ReservationWorkOrder::getServiceStartTime, new Date());
        wrapper.eq(ReservationWorkOrder::getWorkOrderNo, workOrderNo);
        wrapper.eq(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_AWAIT_SERVICE);
        update(wrapper);
    }

    /**
     * 打卡
     *
     * @param workOrderNo 工单号
     */
    @Override
    public Boolean clockIn(String workOrderNo, String clockInPhoto, String clockInRemark) {
        LambdaUpdateWrapper<ReservationWorkOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_SERVICING);
        wrapper.set(ReservationWorkOrder::getClockInStatus, WorkOrderConstants.CLOCK_IN_STATUS_MANUAL);
        if (StrUtil.isNotBlank(clockInPhoto)) {
            wrapper.set(ReservationWorkOrder::getClockInPhoto, systemAttachmentService.clearPrefix(clockInPhoto));
        }
        if (StrUtil.isNotBlank(clockInRemark)) {
            wrapper.set(ReservationWorkOrder::getClockInRemark, clockInRemark);
        }
        wrapper.set(ReservationWorkOrder::getServiceStartTime, new Date());
        wrapper.eq(ReservationWorkOrder::getWorkOrderNo, workOrderNo);
        wrapper.eq(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_AWAIT_SERVICE);
        return update(wrapper);
    }

    /**
     * 服务过程留凭
     */
    @Override
    public Boolean serviceEvidence(String workOrderNo, String serviceEvidenceForm, Integer serviceEvidenceFormId) {
        LambdaUpdateWrapper<ReservationWorkOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ReservationWorkOrder::getServiceEvidenceFormId, serviceEvidenceFormId);
        wrapper.set(ReservationWorkOrder::getServiceEvidenceForm, systemAttachmentService.clearPrefix(serviceEvidenceForm));
        wrapper.eq(ReservationWorkOrder::getWorkOrderNo, workOrderNo);
        wrapper.eq(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_SERVICING);
        return update(wrapper);
    }

    /**
     * 服务结束
     */
    @Override
    public Boolean serviceEnd(String workOrderNo) {
        LambdaUpdateWrapper<ReservationWorkOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_END);
        wrapper.set(ReservationWorkOrder::getServiceEndTime, new Date());
        wrapper.eq(ReservationWorkOrder::getWorkOrderNo, workOrderNo);
        wrapper.eq(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_SERVICING);
        return update(wrapper);
    }

    /**
     * 获取工单分页列表
     *
     * @param request 预约工单列表请求对象
     * @return 工单分页列表
     */
    @Override
    public PageInfo<ReservationWorkOrderPageResponse> getWorkOrderListPage(WorkOrderSearchRequest request, SystemAdmin systemAdmin) {
        // 构建查询条件
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("merId", systemAdmin.getMerId());
        map.put("status", request.getStatus());
        if (StrUtil.isNotBlank(request.getWorkOrderNo())) {
            map.put("workOrderNo", URLUtil.decode(request.getWorkOrderNo()));
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        if (ObjectUtil.isNotNull(request.getServiceType())) {
            map.put("serviceType", request.getServiceType());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT_DATE);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                String startTime = LocalDateTime.parse(dateLimit.getStartTime(), DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT))
                        .format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
                String endTime = LocalDateTime.parse(dateLimit.getEndTime(), DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT))
                        .format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
                map.put("startTime", startTime);
                map.put("endTime", endTime);
            }
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("UserKeywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        Page<ReservationWorkOrder> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<ReservationWorkOrder> list = dao.getWorkOrderListPage(map);

        if (CollectionUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, Collections.emptyList());
        }

        List<ReservationWorkOrderPageResponse> workOrderResponseList = list.stream()
                .map(workOrder -> {
                    OrderDetail orderDetail = orderDetailService.getById(workOrder.getOrderDetailId());
                    ReservationWorkOrderPageResponse reservationWorkOrderResponse = new ReservationWorkOrderPageResponse();
                    BeanUtils.copyProperties(workOrder, reservationWorkOrderResponse);
                    reservationWorkOrderResponse.setProductId(orderDetail.getProductId());
                    reservationWorkOrderResponse.setProductName(orderDetail.getProductName());
                    reservationWorkOrderResponse.setImage(orderDetail.getImage());
                    reservationWorkOrderResponse.setSku(orderDetail.getSku());
                    if (crmebConfig.getPhoneMaskSwitch()) {
                        reservationWorkOrderResponse.setUserPhone(CrmebUtil.maskMobile(reservationWorkOrderResponse.getUserPhone()));
                    }
                    return reservationWorkOrderResponse;
                }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, workOrderResponseList);
    }

    /**
     * 获取商户端预约工单各状态数量
     */
    @Override
    public WorkOrderCountItemResponse getWorkOrderStatusNum(WorkOrderTabsHeaderRequest request, SystemAdmin systemAdmin) {
        Integer merId = systemAdmin.getMerId();
        WorkOrderCountItemResponse countItemResponse = new WorkOrderCountItemResponse();
        countItemResponse.setAll(getWorkOrderCount(request, 0, merId));
        countItemResponse.setUnReceive(getWorkOrderCount(request, 1, merId));
        countItemResponse.setReceived(getWorkOrderCount(request, 2, merId));
        countItemResponse.setInService(getWorkOrderCount(request, 3, merId));
        countItemResponse.setEndService(getWorkOrderCount(request, 4, merId));
        countItemResponse.setRefunded(getWorkOrderCount(request, 9, merId));
        return countItemResponse;
    }

    /**
     * 获取工单详情
     *
     * @param workOrderNo 工单号
     * @return 预约工单详情信息响应对象
     */
    @Override
    public ReservationWorkOrderDetailResponse getInfo(String workOrderNo, SystemAdmin systemAdmin) {
        ReservationWorkOrder reservationWorkOrder = getByWorkOrderNo(workOrderNo);
        ReservationWorkOrderDetailResponse reservationWorkOrderDetailResponse = new ReservationWorkOrderDetailResponse();
        BeanUtils.copyProperties(reservationWorkOrder, reservationWorkOrderDetailResponse);
        // 买家留言
        MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(reservationWorkOrder.getOrderNo());
        if (ObjectUtil.isNotNull(merchantOrder)) {
            reservationWorkOrderDetailResponse.setUserRemark(merchantOrder.getUserRemark());
        }
        // 订单、商品信息
        reservationWorkOrderDetailResponse.setOrderDetail(orderDetailService.getById(reservationWorkOrder.getOrderDetailId()));
        // 服务人员信息
        reservationWorkOrderDetailResponse.setServiceStaff(merchantServiceStaffService.getById(reservationWorkOrder.getServiceStaffId()));
        // 服务时长，单位：分钟
        if (reservationWorkOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_END)) {
            long serviceDuration = DateUtil.between(reservationWorkOrder.getServiceStartTime(), reservationWorkOrder.getServiceEndTime(), DateUnit.MINUTE, true);
            String duration = CrmebDateUtil.minutesToHoursMinutes(serviceDuration);
            reservationWorkOrderDetailResponse.setServiceDuration(duration);
        }
        // 系统表单
        if (reservationWorkOrder.getServiceEvidenceFormId() > 0) {
            SystemForm systemForm = systemFormService.getById(reservationWorkOrder.getServiceEvidenceFormId());
            reservationWorkOrderDetailResponse.setServiceEvidenceFormValue(systemForm.getFormValue());
        }
        if (reservationWorkOrder.getReservationFormId() > 0) {
            SystemForm systemForm = systemFormService.getById(reservationWorkOrder.getReservationFormId());
            reservationWorkOrderDetailResponse.setReservationFormValue(systemForm.getFormValue());
        }

        if (StringUtils.isNotBlank(reservationWorkOrder.getCollaboratorIds())) {
            String[] ids = reservationWorkOrder.getCollaboratorIds().split(",");
            List<MerchantServiceStaff> collaboratorStaffs = merchantServiceStaffService.listByIds(Arrays.asList(ids));
            reservationWorkOrderDetailResponse.setCollaboratorStaffs(collaboratorStaffs);
        }
        if (crmebConfig.getPhoneMaskSwitch()) {
            reservationWorkOrderDetailResponse.setUserPhone(CrmebUtil.maskMobile(reservationWorkOrderDetailResponse.getUserPhone()));
        }
        return reservationWorkOrderDetailResponse;
    }

    /**
     * 商户派单
     *
     * @param request 预约工单派单改派请求对象
     * @return 派单结果
     */
    @Override
    public Boolean assign(ReservationWorkOrderAssignRequest request, SystemAdmin systemAdmin) {
        List<String> workOrderNoList = request.getWorkOrderNoList();
        Integer serviceStaffId = request.getServiceStaffId();

        validateWorkOrderAndStaff(workOrderNoList, serviceStaffId, systemAdmin);
        List<String> updateWorkOrderNoList = new ArrayList<>();

        workOrderNoList.forEach(workOrderNo -> {
            ReservationWorkOrder workOrder = getByWorkOrderNo(workOrderNo);
            if (workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_AWAIT)
                    && !workOrder.getRefundStatus().equals(WorkOrderConstants.REFUND_STATUS_REFUND)) {
                updateWorkOrderNoList.add(workOrderNo);
            }
        });
        if (CollUtil.isEmpty(updateWorkOrderNoList)) {
            return Boolean.TRUE;
        }
        LambdaUpdateWrapper<ReservationWorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ReservationWorkOrder::getWorkOrderNo, updateWorkOrderNoList);
        updateWrapper.set(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_AWAIT_SERVICE)
                .set(ReservationWorkOrder::getServiceStaffId, serviceStaffId)
                .set(ReservationWorkOrder::getAllocateType, WorkOrderConstants.ALLOCATE_TYPE_APPOINT);
        boolean update = update(updateWrapper);
        if (update) {
            // 无需打卡工单，状态自动流转为服务中
            MerchantInfo merchantInfo = merchantInfoService.getByMerId(systemAdmin.getMerId());
            if (merchantInfo.getClockInType().equals(1)) {
                workOrderNoList.forEach(s -> asyncService.workOrderAutoClockIn(s));
            }
        }
        return update;
    }

    /**
     * 商户改派
     *
     * @param request 预约工单派单改派请求对象
     * @return 改派结果
     */
    @Override
    public Boolean reassign(ReservationWorkOrderAssignRequest request, SystemAdmin systemAdmin) {

        List<String> workOrderNoList = request.getWorkOrderNoList();
        Integer serviceStaffId = request.getServiceStaffId();

        List<String> updateWorkOrderNoList = new ArrayList<>();

        validateWorkOrderAndStaff(workOrderNoList, serviceStaffId, systemAdmin);
        workOrderNoList.forEach(workOrderNo -> {
            ReservationWorkOrder workOrder = getByWorkOrderNo(workOrderNo);
            if (ObjectUtil.isNotNull(workOrder) && !Objects.equals(workOrder.getServiceStaffId(), serviceStaffId)
                    && (workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_SERVICING)
                    || workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_AWAIT_SERVICE))
                    && !workOrder.getRefundStatus().equals(WorkOrderConstants.REFUND_STATUS_REFUND)) {
                updateWorkOrderNoList.add(workOrderNo);
            }
        });
        if (CollUtil.isEmpty(updateWorkOrderNoList)) {
            return Boolean.TRUE;
        }
        LambdaUpdateWrapper<ReservationWorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ReservationWorkOrder::getWorkOrderNo, updateWorkOrderNoList);
        updateWrapper.set(ReservationWorkOrder::getServiceStaffId, serviceStaffId)
                .set(ReservationWorkOrder::getCollaboratorIds, null)
                .set(ReservationWorkOrder::getIsEditAllocate, Boolean.TRUE);
        return update(updateWrapper);
    }

    /**
     * 商户改约
     *
     * @param request 改约请求对象
     * @return 改约结果
     */
    @Override
    public Boolean updateAgreement(UpdateAgreementRequest request, SystemAdmin systemAdmin) {
        List<String> workOrderNoList = request.getWorkOrderNoList();

        // 解析预约日期和时段，合并为完整时间点
        String fullDateTimeStr = request.getReservationDate() + " " + request.getReservationTimeSlot().split("-")[0] + ":00";
        Date reservationDateTime = DateUtil.parseDateTime(fullDateTimeStr);
        // 校验预约时间是否小于当前时间
        if (reservationDateTime.before(new Date())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "预约时间不能小于当前时间");
        }
        // 到店
        List<String> updateHomeWorkOrderNoList = new ArrayList<>();
        // 上门
        List<String> updateStoreWorkOrderNoList = new ArrayList<>();
        workOrderNoList.forEach(workOrderNo -> {
            ReservationWorkOrder workOrder = getByWorkOrderNo(workOrderNo);
            if (ObjectUtil.isNotNull(workOrder) && Objects.equals(workOrder.getMerId(), systemAdmin.getMerId())
                    && !workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_END)
                    && !workOrder.getRefundStatus().equals(WorkOrderConstants.REFUND_STATUS_REFUND)) {
                if (workOrder.getServiceType().equals(WorkOrderConstants.SERVICE_TYPE_HOME_SERVICE)) {
                    updateHomeWorkOrderNoList.add(workOrderNo);
                } else {
                    updateStoreWorkOrderNoList.add(workOrderNo);
                }
            }
        });
        if (CollUtil.isNotEmpty(updateHomeWorkOrderNoList)) {
            if (StrUtil.isEmpty(request.getUserAddress())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "含有上门服务工单必须填写用户地址");
            }
        }
        LambdaUpdateWrapper<ReservationWorkOrder> updateHomeWrapper = null;
        LambdaUpdateWrapper<ReservationWorkOrder> updateStoreWrapper = null;
        // 上门服务改约
        if (CollUtil.isNotEmpty(updateHomeWorkOrderNoList)) {
            updateHomeWrapper = new LambdaUpdateWrapper<>();
            updateHomeWrapper.in(ReservationWorkOrder::getWorkOrderNo, updateHomeWorkOrderNoList);
            updateHomeWrapper.set(ReservationWorkOrder::getReservationDate, request.getReservationDate())
                    .set(ReservationWorkOrder::getReservationTimeSlot, request.getReservationTimeSlot())
                    .set(ReservationWorkOrder::getUserName, request.getUserName())
                    .set(ReservationWorkOrder::getUserPhone, request.getUserPhone())
                    .set(ReservationWorkOrder::getUserAddress, request.getUserAddress())
                    .set(ReservationWorkOrder::getIsEditReservation, Boolean.TRUE);
        }
        // 到店服务改约
        if (CollUtil.isNotEmpty(updateStoreWorkOrderNoList)) {
            updateStoreWrapper = new LambdaUpdateWrapper<>();
            updateStoreWrapper.in(ReservationWorkOrder::getWorkOrderNo, updateStoreWorkOrderNoList);
            updateStoreWrapper.set(ReservationWorkOrder::getReservationDate, request.getReservationDate())
                    .set(ReservationWorkOrder::getReservationTimeSlot, request.getReservationTimeSlot())
                    .set(ReservationWorkOrder::getUserName, request.getUserName())
                    .set(ReservationWorkOrder::getUserPhone, request.getUserPhone())
                    .set(ReservationWorkOrder::getIsEditReservation, Boolean.TRUE);
        }
        LambdaUpdateWrapper<ReservationWorkOrder> finalUpdateHomeWrapper = updateHomeWrapper;
        LambdaUpdateWrapper<ReservationWorkOrder> finalUpdateStoreWrapper = updateStoreWrapper;
        return transactionTemplate.execute(status -> {
            boolean homeUpdateResult = true;
            boolean storeUpdateResult = true;
            // 执行上门服务更新（有更新条件才执行）
            if (ObjectUtil.isNotNull(finalUpdateHomeWrapper)) {
                homeUpdateResult = update(finalUpdateHomeWrapper);
            }
            // 执行到店服务更新（有更新条件才执行）
            if (ObjectUtil.isNotNull(finalUpdateStoreWrapper)) {
                storeUpdateResult = update(finalUpdateStoreWrapper);
            }
            return homeUpdateResult && storeUpdateResult;
        });


    }

    /**
     * 到店服务改约
     */
    @Override
    public Boolean updateAgreementByStore(String workOrderNo, String reservationDate, String reservationTimeSlot, String userName, String userPhone) {
        LambdaUpdateWrapper<ReservationWorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ReservationWorkOrder::getReservationDate, reservationDate)
                .set(ReservationWorkOrder::getReservationTimeSlot, reservationTimeSlot)
                .set(ReservationWorkOrder::getUserName, userName)
                .set(ReservationWorkOrder::getUserPhone, userPhone)
                .set(ReservationWorkOrder::getIsEditReservation, Boolean.TRUE);
        updateWrapper.eq(ReservationWorkOrder::getWorkOrderNo, workOrderNo);
        return update(updateWrapper);
    }

    /**
     * 上门服务改约
     */
    @Override
    public Boolean updateAgreementByHome(String workOrderNo, String reservationDate, String reservationTimeSlot, String userName, String userPhone, String userAddress, String longitude, String latitude) {
        LambdaUpdateWrapper<ReservationWorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ReservationWorkOrder::getReservationDate, reservationDate)
                .set(ReservationWorkOrder::getReservationTimeSlot, reservationTimeSlot)
                .set(ReservationWorkOrder::getUserName, userName)
                .set(ReservationWorkOrder::getUserPhone, userPhone)
                .set(ReservationWorkOrder::getUserAddress, userAddress)
                .set(ReservationWorkOrder::getLongitude, longitude)
                .set(ReservationWorkOrder::getLatitude, latitude)
                .set(ReservationWorkOrder::getIsEditReservation, Boolean.TRUE);
        updateWrapper.eq(ReservationWorkOrder::getWorkOrderNo, workOrderNo);
        return update(updateWrapper);
    }

    /**
     * 批量改约时间
     */
    @Override
    public Boolean batchUpdateAgreement(List<String> workOrderNoList, String reservationDate, String reservationTimeSlot) {
        LambdaUpdateWrapper<ReservationWorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ReservationWorkOrder::getReservationDate, reservationDate)
                .set(ReservationWorkOrder::getReservationTimeSlot, reservationTimeSlot)
                .set(ReservationWorkOrder::getIsEditReservation, Boolean.TRUE);
        updateWrapper.in(ReservationWorkOrder::getWorkOrderNo, workOrderNoList);
        return update(updateWrapper);
    }

    /**
     * 商户强制完结工单
     *
     * @param request 强制完结工单请求对象
     * @return 强制完结结果
     */
    @Override
    public Boolean forceFinish(ForceFinishWorkOrderRequest request, SystemAdmin systemAdmin) {
        List<String> updateWorkOrderNoList = new ArrayList<>();
        List<String> orderNoList = new ArrayList<>();
        request.getWorkOrderNoList().forEach(workOrderNo -> {
            ReservationWorkOrder workOrder = getByWorkOrderNo(workOrderNo);
            if (ObjectUtil.isNotNull(workOrder) && Objects.equals(workOrder.getMerId(), systemAdmin.getMerId())
                    && !workOrder.getRefundStatus().equals(WorkOrderConstants.REFUND_STATUS_REFUND)) {
                updateWorkOrderNoList.add(workOrderNo);
                orderNoList.add(workOrder.getOrderNo());
            }
        });
        if (CollUtil.isEmpty(updateWorkOrderNoList)) {
            return Boolean.TRUE;
        }
        // 更新工单状态为完结
        LambdaUpdateWrapper<ReservationWorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ReservationWorkOrder::getWorkOrderNo, updateWorkOrderNoList);
        updateWrapper.set(ReservationWorkOrder::getServiceStatus, WorkOrderConstants.SERVICE_STATUS_END)
                .set(ReservationWorkOrder::getServiceEndTime, new Date());
        Boolean update = update(updateWrapper);
        if (update) {
            orderNoList.stream().distinct().forEach(this::workOrderEndOrderFlow);
        }
        return update;
    }

    /**
     * 工单结束，订单流转
     * @param orderNo 订单编号
     */
    private void workOrderEndOrderFlow(String orderNo) {
        boolean isEnd = true;
        List<ReservationWorkOrder> workOrderList = findByOrderNo(orderNo);
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
    }

    /**
     * 预约工单甘特图
     *
     * @param map 搜索条件
     *            待分配工单为固定查询内容
     */
    @Override
    public List<ReservationWorkOrder> getOverViewData(Map<String, Object> map) {
        return dao.getOverViewData(map);
    }

    /**
     * 根据订单号获取工单列表
     *
     * @param orderNo 订单号
     */
    @Override
    public List<ReservationWorkOrder> findByOrderNo(String orderNo) {
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ReservationWorkOrder::getOrderNo, orderNo);
        lqw.orderByDesc(ReservationWorkOrder::getId);
        return dao.selectList(lqw);
    }

    /**
     * 获取未退款工单列表
     *
     * @param orderNo 订单号
     */
    @Override
    public List<ReservationWorkOrder> findNoRefundByOrderNo(String orderNo) {
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ReservationWorkOrder::getOrderNo, orderNo);
        lqw.eq(ReservationWorkOrder::getRefundStatus, WorkOrderConstants.REFUND_STATUS_NO);
        lqw.orderByDesc(ReservationWorkOrder::getId);
        return dao.selectList(lqw);
    }

    /**
     * 工单批量退款
     *
     * @param workOrderIdList 工单ID列表
     */
    @Override
    public Boolean batchRefundByIdList(List<Integer> workOrderIdList) {
        LambdaUpdateWrapper<ReservationWorkOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ReservationWorkOrder::getRefundStatus, WorkOrderConstants.REFUND_STATUS_REFUND);
        wrapper.set(ReservationWorkOrder::getUpdateTime, new Date());
        wrapper.in(ReservationWorkOrder::getId, workOrderIdList);
        wrapper.eq(ReservationWorkOrder::getRefundStatus, WorkOrderConstants.REFUND_STATUS_NO);
        return update(wrapper);
    }

    /**
     * 获取订单对应的工单数量
     *
     * @param orderNo 订单号
     * @return 工单数量
     */
    @Override
    public Integer getCountByOrderNo(String orderNo) {
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(ReservationWorkOrder::getOrderNo, orderNo);
        return dao.selectCount(lqw);
    }

    /**
     * 是否存在工单
     *
     * @param orderNo 订单号
     */
    @Override
    public Boolean isExistWorkByOrderNo(String orderNo) {
        return getCountByOrderNo(orderNo) > 0;
    }

    /**
     * 通过订单号获取工单ID列表
     *
     * @param orderNo 订单号
     */
    @Override
    public List<Integer> findIdByOrderNo(String orderNo) {
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.select(ReservationWorkOrder::getId);
        lqw.eq(ReservationWorkOrder::getOrderNo, orderNo);
        List<ReservationWorkOrder> workOrderList = dao.selectList(lqw);
        return workOrderList.stream().map(ReservationWorkOrder::getId).collect(Collectors.toList());
    }

    /**
     * 工单备注
     *
     * @param request 备注请求对象
     * @return 备注结果
     */
    @Override
    public Boolean merchantMark(WorkOrderRemarkRequest request, SystemAdmin systemAdmin) {
        ReservationWorkOrder workOrder = getByWorkOrderNo(request.getWorkOrderNo());
        if (ObjectUtil.isNull(workOrder) || !ObjectUtil.equal(workOrder.getMerId(), systemAdmin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单不存在或不属于当前商户");
        }
        return workRemark(request.getWorkOrderNo(), request.getRemark());
    }

    /**
     * 工单备注
     *
     * @param workOrderNo 工单号
     * @param remark      备注
     */
    @Override
    public Boolean workRemark(String workOrderNo, String remark) {
        LambdaUpdateWrapper<ReservationWorkOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ReservationWorkOrder::getRemark, remark);
        wrapper.eq(ReservationWorkOrder::getWorkOrderNo, workOrderNo);
        return update(wrapper);
    }

    /**
     * 校验工单和负责人是否存在
     *
     * @param workOrderNo    工单号
     * @param serviceStaffId 服务员工ID
     * @return 工单对象
     */
    private ReservationWorkOrder validateWorkOrder(String workOrderNo, Integer serviceStaffId, SystemAdmin systemAdmin) {
        MerchantServiceStaff staff = merchantServiceStaffService.getById(serviceStaffId);
        if (ObjectUtil.isNull(staff) || !Objects.equals(staff.getMerId(), systemAdmin.getMerId()) || staff.getIsDelete()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "服务员工不存在或不属于当前商户");
        }
        if (staff.getStatus() != 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该服务员工状态未开启");
        }
        ReservationWorkOrder workOrder = getByWorkOrderNo(workOrderNo);
        if (ObjectUtil.isNull(workOrder) || !Objects.equals(workOrder.getMerId(), systemAdmin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "工单不存在或不属于当前商户");
        }
        return workOrder;
    }

    private void validateWorkOrderAndStaff(List<String> workOrderNoList, Integer serviceStaffId, SystemAdmin systemAdmin) {
        MerchantServiceStaff staff = merchantServiceStaffService.getById(serviceStaffId);
        if (ObjectUtil.isNull(staff) || !Objects.equals(staff.getMerId(), systemAdmin.getMerId()) || staff.getIsDelete()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "服务员工不存在或不属于当前商户");
        }
        if (staff.getStatus() != 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该服务员工状态未开启");
        }
        LambdaQueryWrapper<ReservationWorkOrder> lqw = Wrappers.lambdaQuery();
        lqw.in(ReservationWorkOrder::getWorkOrderNo, workOrderNoList);
        lqw.eq(ReservationWorkOrder::getMerId, systemAdmin.getMerId());
        int count = dao.selectCount(lqw);
        if (count != workOrderNoList.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "存在工单不存在或不属于当前商户");
        }
    }

    /**
     * 提取公共查询条件（复用代码）
     *
     * @param merId   商户ID
     * @param request 甘特图请求对象
     * @return Map<String, Object> 查询条件Map
     */
    private Map<String, Object> buildBaseMap(Integer merId, ServiceStaffWorkOrderSearchRequest request) {
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("merId", merId);
        if (ObjectUtil.isNotEmpty(request.getReservationKeyword())) {
            String keywords = URLUtil.decode(request.getReservationKeyword());
            map.put("reservationKeywords", keywords);
        }
        if (ObjectUtil.isNotNull(request.getServiceType()) && request.getServiceType() > 0) {
            map.put("serviceType", request.getServiceType());
        }
        if (ObjectUtil.isNotEmpty(request.getReservationDate())) {
            map.put("reservationDate", request.getReservationDate());
        }
        if (ObjectUtil.isNotEmpty(request.getServiceStatus())) {
            Set<String> statusSet = Arrays.stream(request.getServiceStatus().split(","))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (statusSet.contains("2")) {
                statusSet.add("1");
            }
            map.put("serviceStatus", statusSet);
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("UserKeywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        return map;
    }

    /**
     * 获取工单总数
     *
     * @param status int 状态
     * @return Integer
     */
    private Integer getWorkOrderCount(WorkOrderTabsHeaderRequest request, int status, Integer merId) {
        // 构建查询条件
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("merId", merId);
        map.put("status", status);
        if (StrUtil.isNotBlank(request.getWorkOrderNo())) {
            map.put("workOrderNo", URLUtil.decode(request.getWorkOrderNo()));
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        if (ObjectUtil.isNotNull(request.getServiceType())) {
            map.put("serviceType", request.getServiceType());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("UserKeywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        return dao.getWorkOrderCount(map);
    }

}

