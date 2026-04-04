package com.zbkj.front.service.staff;

import com.zbkj.common.vo.MyRecord;
import com.zbkj.front.response.ServiceStaffLoginResponse;

import java.util.List;

/**
 * 移动端-服务员工登录服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
public interface StaffLoginService {

    /**
     * 服务员工登录
     */
    ServiceStaffLoginResponse staffLogin();

    ServiceStaffLoginResponse getStaffInfo();
}
