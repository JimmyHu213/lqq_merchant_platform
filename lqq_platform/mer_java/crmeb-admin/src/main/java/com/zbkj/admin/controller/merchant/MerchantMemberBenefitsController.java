package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.request.MerchantMemberBenefitsSaveRequest;
import com.zbkj.admin.service.ManageMerchantMemberBenefitsService;
import com.zbkj.admin.vo.MerchantMemberBenefitsVo;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商户会员权益控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/1
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/member/benefits")
@Api(tags = "商户会员权益控制器")
public class MerchantMemberBenefitsController {

    @Autowired
    private ManageMerchantMemberBenefitsService memberBenefitsService;

    @PreAuthorize("hasAuthority('merchant:member:benefits:list')")
    @ApiOperation(value = "会员权益列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<MerchantMemberBenefitsVo>> getList() {
        return CommonResult.success(memberBenefitsService.getList());
    }

    @PreAuthorize("hasAuthority('merchant:member:benefits:save')")
    @ApiOperation(value = "会员权益保存")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated MerchantMemberBenefitsSaveRequest request) {
        if (memberBenefitsService.save(request.getBenefitsList())) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
