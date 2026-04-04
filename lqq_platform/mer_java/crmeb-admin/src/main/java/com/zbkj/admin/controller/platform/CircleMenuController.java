package com.zbkj.admin.controller.platform;


import com.zbkj.common.model.circle.CircleMenu;
import com.zbkj.common.request.SystemMenuSearchRequest;
import com.zbkj.common.request.circle.CircleMenuRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.MenuCheckVo;
import com.zbkj.service.service.CircleMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 平台端圈层菜单控制器
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
@RequestMapping("api/admin/platform/circle/menu")
@Api(tags = "圈层菜单控制器")
public class CircleMenuController {

    @Autowired
    private CircleMenuService circleMenuService;

    @PreAuthorize("hasAuthority('platform:circle:menu:list')")
    @ApiOperation(value = "菜单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<CircleMenu>> getList(@Validated SystemMenuSearchRequest request) {
        return CommonResult.success(circleMenuService.getCircleMenuList(request));
    }

    @PreAuthorize("hasAuthority('platform:circle:menu:add')")
    @ApiOperation(value = "新增菜单")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated CircleMenuRequest request) {
        if (circleMenuService.addCircleMenu(request)) {
            return CommonResult.success("添加成功");
        }
        return CommonResult.failed("添加失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:menu:delete')")
    @ApiOperation(value = "删除菜单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (circleMenuService.deleteCircleMenu(id)) {
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:menu:update')")
    @ApiOperation(value = "修改菜单")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated CircleMenuRequest request) {
        if (circleMenuService.editCircleMenu(request)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:menu:info')")
    @ApiOperation(value = "菜单详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<CircleMenu> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(circleMenuService.getInfo(id));
    }

    @PreAuthorize("hasAuthority('platform:circle:menu:update:status')")
    @ApiOperation(value = "修改菜单显示状态")
    @RequestMapping(value = "/update/show/{id}", method = RequestMethod.POST)
    public CommonResult<String> updateShowStatus(@PathVariable(value = "id") Integer id) {
        if (circleMenuService.updateCircleShowStatus(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:menu:cache:tree')")
    @ApiOperation(value = "菜单缓存树")
    @RequestMapping(value = "/cache/tree", method = RequestMethod.GET)
    public CommonResult<List<MenuCheckVo>> getCircleMenuCacheTree() {
        return CommonResult.success(circleMenuService.getCircleMenuCacheTree());
    }

}




