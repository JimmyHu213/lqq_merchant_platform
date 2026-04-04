package com.zbkj.front.controller.staff;

import com.zbkj.common.response.SystemFormResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.response.MerchantReservationConfigResponse;
import com.zbkj.front.service.staff.StaffMerchantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务员工商户控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/17
 */
@RestController
@RequestMapping("api/front/staff/merchant")
@Api(tags = "服务员工商户控制器")
public class StaffMerchantController {

    @Autowired
    private StaffMerchantService staffMerchantService;

    @ApiOperation(value = "商户预约设置信息")
    @RequestMapping(value = "/reservation/config", method = RequestMethod.GET)
    public CommonResult<MerchantReservationConfigResponse> getReservationConfig() {
        return CommonResult.success(staffMerchantService.getReservationConfig());
    }

    @ApiOperation(value = "商户预约服务留凭表单信息")
    @RequestMapping(value = "/reservation/service/evidence/form/info", method = RequestMethod.GET)
    public CommonResult<SystemFormResponse> getReservationServiceEvidenceFormInfo() {
        return CommonResult.success(staffMerchantService.getReservationServiceEvidenceFormInfo());
    }
}
