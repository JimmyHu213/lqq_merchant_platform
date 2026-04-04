package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台锁客管理控制器
 * [LQQ-迁移] 自动锁客
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/lock-customer")
@Api(tags = "平台端 - 锁客管理")
public class PlatformLockCustomerController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "全平台锁客记录")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<User>> list(@ModelAttribute PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.isNotNull(User::getLockedMerchantId);
        lqw.orderByDesc(User::getLockedMerchantTime);
        List<User> list = userService.list(lqw);
        return CommonResult.success(CommonPage.restPage(new PageInfo<>(list)));
    }
}
