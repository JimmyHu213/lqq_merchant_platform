package com.zbkj.admin.controller.merchant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.LockCustomerCountResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户锁客管理控制器
 * [LQQ-迁移] 自动锁客
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/lock-customer")
@Api(tags = "商户端 - 锁客管理")
public class MerchantLockCustomerController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "我的锁客用户列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<User>> list(@ModelAttribute PageParamRequest pageParamRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(User::getLockedMerchantId, merId);
        lqw.orderByDesc(User::getLockedMerchantTime);
        List<User> list = userService.list(lqw);
        return CommonResult.success(CommonPage.restPage(new PageInfo<>(list)));
    }

    @ApiOperation(value = "锁客统计")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public CommonResult<LockCustomerCountResponse> count() {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        Integer total = userService.count(Wrappers.<User>lambdaQuery()
                .eq(User::getLockedMerchantId, merId));
        LockCustomerCountResponse response = new LockCustomerCountResponse();
        response.setTotal(total);
        return CommonResult.success(response);
    }
}
