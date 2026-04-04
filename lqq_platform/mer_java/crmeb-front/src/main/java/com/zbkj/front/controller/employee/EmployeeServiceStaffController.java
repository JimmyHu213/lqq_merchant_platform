package com.zbkj.front.controller.employee;

import com.zbkj.common.request.merchant.MerchantServiceStaffSearchRequest;
import com.zbkj.common.response.MerchantServiceStaffResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.employee.EmployeeServiceStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 移动端商家- 服务人员控制器
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
@RestController
@RequestMapping("api/front/employee/service/staff")
@Api(tags = "移动端商家管理 - 服务人员控制器")
public class EmployeeServiceStaffController {

    @Autowired
    private EmployeeServiceStaffService employeeServiceStaffService;

    /**
     * 服务人员列表
     * @param request 搜索条件
     * @author zzp
     */
    @ApiOperation(value = "服务人员列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<MerchantServiceStaffResponse>> getList(@ModelAttribute @Validated MerchantServiceStaffSearchRequest request) {
        return CommonResult.success(employeeServiceStaffService.getList(request));
    }

}
