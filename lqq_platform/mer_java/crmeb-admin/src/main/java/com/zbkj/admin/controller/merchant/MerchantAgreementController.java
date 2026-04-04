package com.zbkj.admin.controller.merchant;

import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

/**
 * 商户协议管理
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/9
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/agreement")
@Api(tags = "协议管理")
public class MerchantAgreementController {

    @Autowired
    private SystemConfigService systemConfigService;

    @PreAuthorize("hasAuthority('merchant:agreement:member:open:card:save')")
    @ApiOperation(value = "商户会员开卡协议保存")
    @RequestMapping(value = "/member/card/open/save", method = RequestMethod.POST)
    public CommonResult<Boolean> merchantMemberCardOpenSave(@RequestBody @NotEmpty String agreement) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(SysConfigConstants.MERCHANT_MEMBER_CARD_OPEN_AGREEMENT, agreement, admin.getMerId()));
    }

    @PreAuthorize("hasAuthority('merchant:agreement:member:open:card:info')")
    @ApiOperation(value = "商户会员开卡协议详情")
    @RequestMapping(value = "/member/card/open/info", method = RequestMethod.GET)
    public CommonResult<String> merchantMemberCardOpenInfo() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.MERCHANT_MEMBER_CARD_OPEN_AGREEMENT, admin.getMerId()));
    }

}
