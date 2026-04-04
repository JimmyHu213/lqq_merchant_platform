package com.zbkj.admin.controller.circle;

import com.zbkj.common.model.circle.CircleAdmin;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.SystemAdminResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CircleAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 圈层端-管理员控制器
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
@RequestMapping("api/admin/circle/admin")
@Api(tags = "管理员控制器")
public class CircleAdminController {
    
    @Autowired
    private CircleAdminService circleAdminService;

    @PreAuthorize("hasAuthority('circle:admin:list')")
    @ApiOperation(value = "管理员分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemAdminResponse>> getList(@Validated SystemAdminRequest systemAdminRequest, @Validated PageParamRequest pageParamRequest) {
        CommonPage<SystemAdminResponse> systemAdminCommonPage = CommonPage.restPage(circleAdminService.getCircleAdminList(systemAdminRequest, pageParamRequest));
        return CommonResult.success(systemAdminCommonPage);
    }

    @PreAuthorize("hasAuthority('circle:admin:save')")
    @ApiOperation(value = "新增后台管理员")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated @RequestBody SystemAdminAddRequest systemAdminAddRequest) {
        if (circleAdminService.saveCircleAdmin(systemAdminAddRequest)) {
            return CommonResult.success("添加管理员成功");
        }
        return CommonResult.failed("添加管理员失败");
    }

    @PreAuthorize("hasAuthority('circle:admin:delete')")
    @ApiOperation(value = "删除管理员")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (circleAdminService.removeCircleAdmin(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('circle:admin:update')")
    @ApiOperation(value = "修改管理员")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated SystemAdminUpdateRequest systemAdminRequest) {
        if (circleAdminService.updateCircleAdmin(systemAdminRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('circle:admin:info')")
    @ApiOperation(value = "后台管理员详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<CircleAdmin> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(circleAdminService.getDetail(id));
    }

    @PreAuthorize("hasAuthority('circle:admin:update:status')")
    @ApiOperation(value = "修改后台管理员状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.GET)
    public CommonResult<String> updateStatus(@PathVariable(value = "id") Integer id,
                                             @RequestParam(value = "status") Boolean status) {
        if (circleAdminService.updateCircleAdminStatus(id, status)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @PreAuthorize("hasAuthority('circle:admin:update:password')")
    @ApiOperation(value = "修改后台管理员密码")
    @RequestMapping(value = "/update/password", method = RequestMethod.POST)
    public CommonResult<String> updatePassword(@RequestBody @Validated SystemAdminUpdatePwdRequest request) {
        if (circleAdminService.updateCircleAdminPassword(request)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }


}
