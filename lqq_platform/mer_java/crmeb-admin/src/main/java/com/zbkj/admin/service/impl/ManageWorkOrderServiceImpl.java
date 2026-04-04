package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.ManageWorkOrderService;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.constants.WorkOrderConstants;
import com.zbkj.common.dto.Location;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.reservation.MerchantServiceStaff;
import com.zbkj.common.model.reservation.ReservationWorkOrder;
import com.zbkj.common.request.reservation.*;
import com.zbkj.common.response.reservation.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.AddressUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.service.service.MerchantServiceStaffService;
import com.zbkj.service.service.OrderDetailService;
import com.zbkj.service.service.ReservationWorkOrderService;
import com.zbkj.service.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 关断端工单服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/13
 */
@Slf4j
@Service
public class ManageWorkOrderServiceImpl implements ManageWorkOrderService {

    @Autowired
    private ReservationWorkOrderService workOrderService;
    @Autowired
    private MerchantServiceStaffService merchantServiceStaffService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private SystemConfigService systemConfigService;


    /**
     * 获取工单分页列表
     *
     * @param request 预约工单列表请求对象
     * @return 工单分页列表
     */
    @Override
    public PageInfo<ReservationWorkOrderPageResponse> getWorkOrderListPage(WorkOrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return workOrderService.getWorkOrderListPage(request, systemAdmin);
    }

    /**
     * 获取商户端预约工单各状态数量
     */
    @Override
    public WorkOrderCountItemResponse getWorkOrderStatusNum(WorkOrderTabsHeaderRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return workOrderService.getWorkOrderStatusNum(request, systemAdmin);
    }

    /**
     * 获取工单详情
     *
     * @param workOrderNo 工单号
     * @return 预约工单详情信息响应对象
     */
    @Override
    public ReservationWorkOrderDetailResponse getInfo(String workOrderNo) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return workOrderService.getInfo(workOrderNo, systemAdmin);
    }

    /**
     * 工单备注
     *
     * @param request 备注请求对象
     * @return 备注结果
     */
    @Override
    public Boolean merchantMark(WorkOrderRemarkRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return workOrderService.merchantMark(request, systemAdmin);
    }

    /**
     * 商户派单
     *
     * @param request 预约工单派单改派请求对象
     * @return 派单结果
     */
    @Override
    public Boolean assign(ReservationWorkOrderAssignRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return workOrderService.assign(request, systemAdmin);
    }

    /**
     * 商户改派
     *
     * @param request 预约工单派单改派请求对象
     * @return 改派结果
     */
    @Override
    public Boolean reassign(ReservationWorkOrderAssignRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return workOrderService.reassign(request, systemAdmin);
    }

    /**
     * 商户改约
     *
     * @param request 改约请求对象
     * @return 改约结果
     */
    @Override
    public Boolean updateAgreement(UpdateAgreementRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        // 解析预约日期和时段，合并为完整时间点
        String fullDateTimeStr = request.getReservationDate() + " " + request.getReservationTimeSlot().split("-")[0].trim() + ":00";
        Date reservationDateTime = DateUtil.parseDateTime(fullDateTimeStr);
        // 校验预约时间是否小于当前时间
        if (reservationDateTime.before(new Date())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "预约时间不能小于当前时间");
        }
        if (request.getWorkOrderNoList().size() > 1 || Boolean.TRUE.equals(request.getIsBatch())) {
            return workOrderService.batchUpdateAgreement(request.getWorkOrderNoList(), request.getReservationDate(), request.getReservationTimeSlot());
        }
        // 单个工单改约，校验改约信息是否完整
        if (StrUtil.isBlank(request.getUserName()) || StrUtil.isBlank(request.getUserPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "预约人姓名和电话不能为空");
        }
        ReservationWorkOrder workOrder = workOrderService.getByWorkOrderNo(request.getWorkOrderNoList().get(0));
        if (workOrder.getServiceType().equals(WorkOrderConstants.SERVICE_TYPE_HOME_SERVICE)) {
            if (StrUtil.isBlank(request.getUserAddress())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "上门服务工单必须填写用户地址");
            }
            String address = URLUtil.decode(request.getUserAddress());
            String longitude = workOrder.getLongitude();
            String latitude = workOrder.getLatitude();
            if (!workOrder.getUserAddress().equals(address)) {
                // 通过腾讯地图，反解析地址获取经纬度
                try {
                    String txKey = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_SITE_TENG_XUN_MAP_KEY);
                    Location location = AddressUtil.getLocationByTx(txKey, address);
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                } catch (Exception e) {
                    log.error("通过腾讯地图，解析地址获取经纬度失败！改约工单号 = {}", workOrder.getWorkOrderNo());
                    log.error("通过腾讯地图，解析地址获取经纬度失败！", e);
                    longitude = "0";
                    latitude = "0";
                }
            }
            return workOrderService.updateAgreementByHome(workOrder.getWorkOrderNo(), request.getReservationDate(),
                    request.getReservationTimeSlot(), request.getUserName(), request.getUserPhone(), request.getUserAddress(), longitude, latitude);
        }
        return workOrderService.updateAgreementByStore(workOrder.getWorkOrderNo(), request.getReservationDate(),
                request.getReservationTimeSlot(), request.getUserName(), request.getUserPhone());
    }

    /**
     * 商户强制完结工单
     *
     * @param request 强制完结工单请求对象
     * @return 强制完结结果
     */
    @Override
    public Boolean forceFinish(ForceFinishWorkOrderRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return workOrderService.forceFinish(request, systemAdmin);
    }

    /**
     * 预约工单甘特图
     *
     * @param request 搜索条件
     * @return ReservationStatisticsResponse 预约工单统计响应对象
     */
    @Override
    public ReservationStatisticsResponse getOverView(ServiceStaffWorkOrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        Map<String, Object> map = buildOverViewMap(merId, request);
        List<ReservationWorkOrder> workOrderList = workOrderService.getOverViewData(map);
        return buildOverViewResponse(merId, workOrderList);
    }

    /**
     * 预约工单甘特图-数量统计
     *
     * @param request 搜索条件
     * @return WorkOrderCountViewResponse 预约工单-甘特图状态数量响应对象
     */
    @Override
    public WorkOrderCountViewResponse getViewNum(ServiceStaffWorkOrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        Map<String, Object> map = buildOverViewMap(merId, request);
        List<ReservationWorkOrder> workOrderList = workOrderService.getOverViewData(map);
        return buildViewNumResponse(workOrderList);
    }

    /**
     * 构建甘特图-数量统计响应对象
     *
     * @param workOrderList 工单列表
     */
    private WorkOrderCountViewResponse buildViewNumResponse(List<ReservationWorkOrder> workOrderList) {
        WorkOrderCountViewResponse response = new WorkOrderCountViewResponse();
        Integer unReserve = 0;
        Integer toService = 0;
        Integer servicing = 0;
        Integer serviced = 0;
        for (ReservationWorkOrder workOrder : workOrderList) {
            if (workOrder.getAllocateType().equals(WorkOrderConstants.ALLOCATE_TYPE_NO) || workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_AWAIT) ) {
                unReserve++;
                continue;
            }
            if (workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_AWAIT_SERVICE)) {
                toService++;
            } else if (workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_SERVICING)) {
                servicing++;
            } else if (workOrder.getServiceStatus().equals(WorkOrderConstants.SERVICE_STATUS_END)) {
                serviced++;
            }
        }
        response.setUnReserve(unReserve);
        response.setToService(toService);
        response.setServicing(servicing);
        response.setServiced(serviced);
        return response;
    }

    /**
     * 构建甘特图响应对象
     *
     * @param merId         商户ID
     * @param workOrderList 工单列表
     */
    private ReservationStatisticsResponse buildOverViewResponse(Integer merId, List<ReservationWorkOrder> workOrderList) {
        ReservationStatisticsResponse response = new ReservationStatisticsResponse();
        // 服务员工
        List<ServiceStaffWorkOrderResponse> staffResponseList = new ArrayList<>();
        ServiceStaffWorkOrderResponse awaitResponse = new ServiceStaffWorkOrderResponse();
        awaitResponse.setName("未指派");
        awaitResponse.setStaffId(0);
        awaitResponse.setMerId(merId);
        awaitResponse.setWorkOrderList(new ArrayList<>());
        staffResponseList.add(awaitResponse);
        List<MerchantServiceStaff> staffList = merchantServiceStaffService.findByMerId(merId);
        if (CollUtil.isNotEmpty(staffList)) {
            staffList = staffList.stream().sorted(Comparator.comparing(MerchantServiceStaff::getSort).reversed()).collect(Collectors.toList());
            staffList.forEach(staff -> {
                ServiceStaffWorkOrderResponse staffResponse = new ServiceStaffWorkOrderResponse();
                staffResponse.setName(staff.getName());
                staffResponse.setStaffId(staff.getId());
                staffResponse.setMerId(merId);
                staffResponse.setWorkOrderList(new ArrayList<>());
                staffResponseList.add(staffResponse);
            });
        }

        // 工单归档
        if (CollUtil.isEmpty(workOrderList)) {
            response.setCount(0);
            response.setList(staffResponseList);
            return response;
        }
        response.setCount(workOrderList.size());
        Map<Integer, List<ReservationWorkOrderPageResponse>> workOrderStaffMap = workOrderList.stream()
                .map(workOrder -> {
                    OrderDetail orderDetail = orderDetailService.getById(workOrder.getOrderDetailId());
                    ReservationWorkOrderPageResponse pageResponse = new ReservationWorkOrderPageResponse();
                    BeanUtils.copyProperties(workOrder, pageResponse);
                    pageResponse.setProductId(orderDetail.getProductId());
                    pageResponse.setProductName(orderDetail.getProductName());
                    pageResponse.setImage(orderDetail.getImage());
                    pageResponse.setSku(orderDetail.getSku());
                    return pageResponse;
                }).collect(Collectors.groupingBy(ReservationWorkOrderPageResponse::getServiceStaffId));
        for (ServiceStaffWorkOrderResponse staffWorkOrderResponse : staffResponseList) {
            List<ReservationWorkOrderPageResponse> orderList = workOrderStaffMap.get(staffWorkOrderResponse.getStaffId());
            if (CollUtil.isNotEmpty(orderList)) {
                staffWorkOrderResponse.setWorkOrderList(orderList);
                workOrderStaffMap.remove(staffWorkOrderResponse.getStaffId());
            }
        }
        if (workOrderStaffMap.size() > 0) {
            // 有已删除员工数据
            Set<Integer> keySet = workOrderStaffMap.keySet();
            List<Integer> otherStaffIdList = new ArrayList<>(keySet);
            List<MerchantServiceStaff> otherStaffList = merchantServiceStaffService.findByStaffIdList(otherStaffIdList);
            otherStaffList.forEach(staff -> {
                ServiceStaffWorkOrderResponse staffResponse = new ServiceStaffWorkOrderResponse();
                staffResponse.setName(staff.getName());
                staffResponse.setStaffId(staff.getId());
                staffResponse.setMerId(merId);
                staffResponse.setWorkOrderList(workOrderStaffMap.get(staff.getId()));
                staffResponseList.add(staffResponse);
            });
        }
        response.setList(staffResponseList);
        return response;
    }

    /**
     * 构建工单甘特图查询Map对象
     */
    private Map<String, Object> buildOverViewMap(Integer merId, ServiceStaffWorkOrderSearchRequest request) {
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("merId", merId);
        if (StrUtil.isNotBlank(request.getReservationKeyword())) {
            String keywords = URLUtil.decode(request.getReservationKeyword());
            map.put("reservationKeywords", keywords);
        }
        if (ObjectUtil.isNotNull(request.getStaffId())) {
            map.put("staffId", request.getStaffId());
        }
        if (ObjectUtil.isNotNull(request.getServiceType()) && request.getServiceType() > 0) {
            map.put("serviceType", request.getServiceType());
        }
        if (StrUtil.isNotBlank(request.getReservationDate())) {
            map.put("reservationDate", request.getReservationDate());
        } else {
            map.put("reservationDate", DateUtil.format(new Date(), "yyyy-MM-dd"));
        }
        if (StrUtil.isNotBlank(request.getServiceStatus())) {
            List<Integer> statusList = CrmebUtil.stringToArray(request.getServiceStatus());
            statusList.add(1);
            map.put("serviceStatusList", statusList);
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
}
