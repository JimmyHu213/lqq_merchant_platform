package com.zbkj.front.controller.staff;

import com.zbkj.common.result.CommonResult;
import com.zbkj.front.response.ServiceStaffLoginResponse;
import com.zbkj.front.service.staff.StaffLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务员工登录控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@RestController
@RequestMapping("api/front/staff/login")
@Api(tags = "服务员工登录控制器")
public class StaffLoginController {

    @Autowired
    private StaffLoginService staffLoginService;

    @ApiOperation(value = "服务员工登录")
    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public CommonResult<ServiceStaffLoginResponse> serviceStaffLogin() {
        return CommonResult.success(staffLoginService.staffLogin());
    }

    @ApiOperation(value = "获取登录员工信息")
    @RequestMapping(value = "/staff/info", method = RequestMethod.GET)
    public CommonResult<ServiceStaffLoginResponse> getStaffInfo() {
        return CommonResult.success(staffLoginService.getStaffInfo());
    }
}
