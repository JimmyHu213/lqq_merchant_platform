package com.zbkj.front.controller.staff;

import com.zbkj.common.dto.AntiResolutionAddress;
import com.zbkj.common.dto.Location;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.staff.StaffAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务员工地址控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/7
 */
@RestController
@RequestMapping("api/front/staff/address")
@Api(tags = "服务员工地址控制器")
public class StaffAddressController {

    @Autowired
    private StaffAddressService staffAddressService;

    @ApiOperation(value = "通过坐标获取地址")
    @RequestMapping(value = "/get/coordinate/address", method = RequestMethod.GET)
    public CommonResult<AntiResolutionAddress> getAddressByCoordinate(@ModelAttribute @Validated Location location) {
        return CommonResult.success(staffAddressService.getAddressByCoordinate(location));
    }

}
