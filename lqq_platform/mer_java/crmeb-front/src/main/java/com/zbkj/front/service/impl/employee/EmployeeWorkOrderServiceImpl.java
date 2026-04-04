package com.zbkj.front.service.impl.employee;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.WorkOrderConstants;
import com.zbkj.common.dto.Location;
import com.zbkj.common.enums.MerchantEmployee;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.reservation.ReservationWorkOrder;
import com.zbkj.common.request.reservation.*;
import com.zbkj.common.response.reservation.ReservationWorkOrderDetailResponse;
import com.zbkj.common.response.reservation.ReservationWorkOrderPageResponse;
import com.zbkj.common.response.reservation.WorkOrderCountItemResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.AddressUtil;
import com.zbkj.front.service.employee.EmployeeWorkOrderService;
import com.zbkj.service.service.MerchantEmployeeService;
import com.zbkj.service.service.ReservationWorkOrderService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * EmployeeWorkerOrderServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@Service
public class EmployeeWorkOrderServiceImpl implements EmployeeWorkOrderService {

    @Autowired
    private ReservationWorkOrderService reservationWorkOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantEmployeeService merchantEmployeeService;
    @Autowired
    private SystemConfigService systemConfigService;


    @Override
    public PageInfo<ReservationWorkOrderPageResponse> getWorkOrderListPage(WorkOrderSearchRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_WORK_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return reservationWorkOrderService.getWorkOrderListPage(request, systemAdmin);
    }

    @Override
    public ReservationWorkOrderDetailResponse getInfo(String workOrderNo) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_WORK_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return reservationWorkOrderService.getInfo(workOrderNo, systemAdmin);
    }

    @Override
    public Boolean assign(ReservationWorkOrderAssignRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_WORK_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return reservationWorkOrderService.assign(request, systemAdmin);
    }

    @Override
    public Boolean reassign(ReservationWorkOrderAssignRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_WORK_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return reservationWorkOrderService.reassign(request, systemAdmin);
    }

    @Override
    public Boolean updateAgreement(UpdateAgreementRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_WORK_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        // 解析预约日期和时段，合并为完整时间点
        String fullDateTimeStr = request.getReservationDate() + " " + request.getReservationTimeSlot().split("-")[0].trim() + ":00";
        Date reservationDateTime = DateUtil.parseDateTime(fullDateTimeStr);
        // 校验预约时间是否小于当前时间
        if (reservationDateTime.before(new Date())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "预约时间不能小于当前时间");
        }
        if (request.getWorkOrderNoList().size() > 1 || Boolean.TRUE.equals(request.getIsBatch())) {
            return reservationWorkOrderService.batchUpdateAgreement(request.getWorkOrderNoList(), request.getReservationDate(), request.getReservationTimeSlot());
        }
        // 单个工单改约，校验改约信息是否完整
        if (StrUtil.isBlank(request.getUserName()) || StrUtil.isBlank(request.getUserPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "预约人姓名和电话不能为空");
        }
        ReservationWorkOrder workOrder = reservationWorkOrderService.getByWorkOrderNo(request.getWorkOrderNoList().get(0));
        if (workOrder.getServiceType().equals(WorkOrderConstants.SERVICE_TYPE_HOME_SERVICE)) {
            if (StrUtil.isBlank(request.getUserAddress())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "含有上门服务工单必须填写用户地址");
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
            return reservationWorkOrderService.updateAgreementByHome(workOrder.getWorkOrderNo(), request.getReservationDate(),
                    request.getReservationTimeSlot(), request.getUserName(), request.getUserPhone(), request.getUserAddress(), longitude, latitude);
        }
        return reservationWorkOrderService.updateAgreementByStore(workOrder.getWorkOrderNo(), request.getReservationDate(),
                request.getReservationTimeSlot(), request.getUserName(), request.getUserPhone());
    }

    @Override
    public Boolean forceFinish(ForceFinishWorkOrderRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_WORK_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return reservationWorkOrderService.forceFinish(request, systemAdmin);
    }

    @Override
    public WorkOrderCountItemResponse getWorkOrderStatusNum(WorkOrderTabsHeaderRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_WORK_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return reservationWorkOrderService.getWorkOrderStatusNum(request, systemAdmin);
    }

    @Override
    public Boolean merchantMark(WorkOrderRemarkRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_WORK_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return reservationWorkOrderService.merchantMark(request, systemAdmin);
    }
}
