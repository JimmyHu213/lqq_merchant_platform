package com.zbkj.front.controller;

import com.zbkj.common.result.CommonResult;
import com.zbkj.front.response.BecomeMerchantMemberResponse;
import com.zbkj.front.response.MerchantMemberLevelBenefitsResponse;
import com.zbkj.front.response.MerchantMemberUserResponse;
import com.zbkj.front.service.FrontMerchantMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商户会员控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/10
 */
@Slf4j
@RestController
@RequestMapping("api/front/merchant/member")
@Api(tags = "商户会员控制器")
public class MerchantMemberController {

    @Autowired
    private FrontMerchantMemberService merchantMemberService;

    @ApiOperation(value = "成为商户会员")
    @RequestMapping(value = "/become/{merId}", method = RequestMethod.POST)
    public CommonResult<BecomeMerchantMemberResponse> becomeMember(@PathVariable(value = "merId") Integer merId) {
        return CommonResult.success(merchantMemberService.becomeMember(merId));
    }

    @ApiOperation(value = "获取商户会员用户信息")
    @RequestMapping(value = "/user/info/{merId}", method = RequestMethod.GET)
    public CommonResult<MerchantMemberUserResponse> memberUserInfo(@PathVariable(value = "merId") Integer merId) {
        return CommonResult.success(merchantMemberService.getMemberUserInfo(merId));
    }

    @ApiOperation(value = "获取商户会员等级列表")
    @RequestMapping(value = "/level/list/{merId}", method = RequestMethod.GET)
    public CommonResult<List<MerchantMemberLevelBenefitsResponse>> findMemberLevelList(@PathVariable(value = "merId") Integer merId) {
        return CommonResult.success(merchantMemberService.findMemberLevelList(merId));
    }

    @ApiOperation(value = "商户会员注销")
    @RequestMapping(value = "/logoff/{merId}", method = RequestMethod.POST)
    public CommonResult<Boolean> logoffMember(@PathVariable(value = "merId") Integer merId) {
        return CommonResult.success(merchantMemberService.logoffMember(merId));
    }
}
