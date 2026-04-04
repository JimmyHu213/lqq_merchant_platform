package com.zbkj.admin.controller.circle;

import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.MenuCheckVo;
import com.zbkj.service.service.CircleMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 圈层端菜单控制器
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
@RequestMapping("api/admin/circle/menu")
@Api(tags = "菜单控制器")
public class CircleMenuManageController {
    @Autowired
    private CircleMenuService circleMenuService;


    @PreAuthorize("hasAuthority('circle:menu:cache:tree')")
    @ApiOperation(value = "当前圈层菜单缓存树")
    @RequestMapping(value = "/cache/tree", method = RequestMethod.GET)
    public CommonResult<List<MenuCheckVo>> getCircleNodeMenuCacheTree() {
        return CommonResult.success(circleMenuService.getCircleMenuCacheList());
    }


}