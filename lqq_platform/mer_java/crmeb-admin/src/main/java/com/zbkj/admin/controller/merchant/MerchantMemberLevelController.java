package com.zbkj.admin.controller.merchant;

import com.zbkj.admin.request.MerchantMemberLevelSaveRequest;
import com.zbkj.admin.response.MerchantMemberLevelSaveResponse;
import com.zbkj.admin.service.ManageMerchantMemberLevelService;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/3
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/member/level")
@Api(tags = "商户会员等级控制器")
public class MerchantMemberLevelController {

    @Autowired
    private ManageMerchantMemberLevelService memberLevelService;

    @PreAuthorize("hasAuthority('merchant:member:level:list')")
    @ApiOperation(value = "会员等级列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<MerchantMemberLevelSaveResponse>> getList() {
        return CommonResult.success(memberLevelService.getList());
    }

    @PreAuthorize("hasAuthority('merchant:member:level:add')")
    @ApiOperation(value = "会员等级新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated MerchantMemberLevelSaveRequest request) {
        if (memberLevelService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:member:level:update')")
    @ApiOperation(value = "会员等级编辑")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated MerchantMemberLevelSaveRequest request) {
        if (memberLevelService.update(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('merchant:member:level:delete')")
    @ApiOperation(value = "会员等级删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable("id") Integer id) {
        if (memberLevelService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
