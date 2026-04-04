package com.zbkj.admin.controller.merchant;

import com.zbkj.common.request.reservation.MerchantReservationToHomeRequest;
import com.zbkj.common.request.reservation.MerchantReservationToStoreRequest;
import com.zbkj.common.response.reservation.MerchantReservationToStoreResponse;
import com.zbkj.common.response.reservation.MerchantReservationToHomeResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/admin/merchant/reservation/config")
@Api(tags = "商家端 - 预约设置管理")
public class MerchantReservationConfigController {

    @Autowired
    private MerchantInfoService merchantInfoService;

    @PreAuthorize("hasAuthority('admin:merchant:reservation:to:home:info')")
    @ApiOperation(value = "上门服务详情")
    @RequestMapping(value = "/home/info", method = RequestMethod.GET)
    public CommonResult<MerchantReservationToHomeResponse> info() {
        return CommonResult.success(merchantInfoService.getReservationConfigInfo());
    }

    @PreAuthorize("hasAuthority('admin:merchant:reservation:to:home:edit')")
    @ApiOperation(value = "编辑上门服务预约信息")
    @RequestMapping(value = "/home/edit", method = RequestMethod.POST)
    public CommonResult<String> edit(@RequestBody @Validated MerchantReservationToHomeRequest merchantReservationToHomeRequest) {
        if(merchantInfoService.editReservationConfig(merchantReservationToHomeRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('admin:merchant:reservation:to:store:info')")
    @ApiOperation(value = "到店服务详情")
    @RequestMapping(value = "/store/info", method = RequestMethod.GET)
    public CommonResult<MerchantReservationToStoreResponse> toStoreInfo() {
        return CommonResult.success(merchantInfoService.getReservationConfigToStoreInfo());
    }

    @PreAuthorize("hasAuthority('admin:merchant:reservation:to:store:edit')")
    @ApiOperation(value = "编辑到店服务预约信息")
    @RequestMapping(value = "/store/edit", method = RequestMethod.POST)
    public CommonResult<String> editToStore(@RequestBody @Validated MerchantReservationToStoreRequest merchantReservationRequest) {
        if(merchantInfoService.editToStoreReservationConfig(merchantReservationRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }


}
