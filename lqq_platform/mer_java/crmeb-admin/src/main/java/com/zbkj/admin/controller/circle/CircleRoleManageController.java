package com.zbkj.admin.controller.circle;


import com.zbkj.common.model.circle.CircleRole;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemRoleRequest;
import com.zbkj.common.request.SystemRoleSearchRequest;
import com.zbkj.common.request.SystemRoleStatusRequest;
import com.zbkj.common.response.RoleInfoResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CircleRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 圈层端角色控制器
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
@RequestMapping("api/admin/circle/role")
@Api(tags = "角色控制器")
public class CircleRoleManageController {
    
    @Autowired
    private CircleRoleService circleRoleService;

    @PreAuthorize("hasAuthority('circle:role:list')")
    @ApiOperation(value = "角色分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<CircleRole>> getList(@Validated SystemRoleSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<CircleRole> systemRoleCommonPage = CommonPage.restPage(circleRoleService.getCircleRoleList(request, pageParamRequest));
        return CommonResult.success(systemRoleCommonPage);
    }


    @PreAuthorize("hasAuthority('circle:role:save')")
    @ApiOperation(value = "新增角色")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SystemRoleRequest systemRoleRequest) {
        if (circleRoleService.addCircleRole(systemRoleRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('circle:role:delete')")
    @ApiOperation(value = "删除角色")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (circleRoleService.deleteCircleRole(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('circle:role:update')")
    @ApiOperation(value = "修改角色")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated SystemRoleRequest systemRoleRequest) {
        if (circleRoleService.editCircleRole(systemRoleRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('circle:role:info')")
    @ApiOperation(value = "角色详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<RoleInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(circleRoleService.getInfo(id));
    }

    @PreAuthorize("hasAuthority('circle:role:update:status')")
    @ApiOperation(value = "修改角色状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@Validated @RequestBody SystemRoleStatusRequest request) {
        if (circleRoleService.updateStatus(request)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }


}
