package com.zbkj.admin.controller.merchant;


import com.zbkj.common.model.user.UserShoppingCreditsRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.merchant.MerchantUserSearchRequest;
import com.zbkj.common.response.MerchantUserDetailResponse;
import com.zbkj.common.response.MerchantUserPageResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户端用户控制器
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
@RequestMapping("api/admin/merchant/user")
@Api(tags = "商户端用户控制器")
@Validated
public class MerchantUserController {

    @Autowired
    private MerchantUserService merchantUserService;

    @PreAuthorize("hasAuthority('merchant:user:page:list')")
    @ApiOperation(value = "商户用户分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantUserPageResponse>> findUserPage(@ModelAttribute @Validated MerchantUserSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(merchantUserService.findUserPage(request)));
    }

    @PreAuthorize("hasAuthority('merchant:user:detail')")
    @ApiOperation(value = "商户用户详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<MerchantUserDetailResponse> detail(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(merchantUserService.getMerchantUserDetail(id));
    }

    @PreAuthorize("hasAuthority('merchant:user:shopping:credits:record')")
    @ApiOperation(value = "商户用户购物金记录")
    @RequestMapping(value = "/shopping/credits/record/{id}", method = RequestMethod.GET)
    public CommonResult<List<UserShoppingCreditsRecord>> findUserShoppingCreditsRecord(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(merchantUserService.findMerchantUserShoppingCreditsRecord(id));
    }

    @PreAuthorize("hasAuthority('merchant:user:shopping:credits:freeze:update')")
    @ApiOperation(value = "商户用户购物金冻结/解冻")
    @RequestMapping(value = "/shopping/credits/freeze/update/{id}", method = RequestMethod.POST)
    public CommonResult<List<UserShoppingCreditsRecord>> userShoppingCreditsFreezeUpdate(@PathVariable(value = "id") Integer id) {
        if (merchantUserService.userShoppingCreditsFreezeUpdate(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



