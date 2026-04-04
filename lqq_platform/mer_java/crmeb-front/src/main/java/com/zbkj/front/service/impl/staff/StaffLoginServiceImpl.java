package com.zbkj.front.service.impl.staff;

import cn.hutool.core.util.ObjectUtil;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.reservation.MerchantServiceStaff;
import com.zbkj.common.model.user.User;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.front.response.ServiceStaffLoginResponse;
import com.zbkj.front.service.staff.StaffLoginService;
import com.zbkj.service.service.MerchantService;
import com.zbkj.service.service.MerchantServiceStaffService;
import com.zbkj.service.service.ReservationWorkOrderService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 移动端-服务员工登录服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@Service
public class StaffLoginServiceImpl implements StaffLoginService {

    @Autowired
    private UserService userService;
    @Autowired
    private MerchantServiceStaffService merchantServiceStaffService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ReservationWorkOrderService reservationWorkOrderService;
    @Autowired
    private FrontTokenComponent tokenComponent;


    /**
     * 服务员工登录
     */
    @Override
    public ServiceStaffLoginResponse staffLogin() {
        User user = userService.getInfo();
        MerchantServiceStaff staff = merchantServiceStaffService.getByUserId(user.getId());
        if (ObjectUtil.isNull(staff)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "你还不是商户员工");
        }
        if (staff.getStatus().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "员工状态异常，请联系商户");
        }
        ServiceStaffLoginResponse response = new ServiceStaffLoginResponse();
        response.setToken(tokenComponent.createServiceStaffToken(staff));
        return response;
    }

    @Override
    public ServiceStaffLoginResponse getStaffInfo() {
        Integer staffId = tokenComponent.getServiceStaffId();
        MerchantServiceStaff staff = merchantServiceStaffService.getById(staffId);
        Merchant merchant = merchantService.getById(staff.getMerId());
        ServiceStaffLoginResponse response = new ServiceStaffLoginResponse();
        response.setId(staff.getId());
        response.setName(staff.getName());
        response.setIdPhoto(staff.getIdPhoto());
        response.setMerId(staff.getMerId());
        response.setMerName(merchant.getName());
        response.setAwaitWorkNum(reservationWorkOrderService.getAwaitCountByStaffId(staff.getId()));
        response.setWorkingNum(reservationWorkOrderService.getWorkingCountByStaffId(staff.getId()));
        response.setWorkedNum(reservationWorkOrderService.getWorkedCountByStaffId(staff.getId()));
        return response;
    }
}
