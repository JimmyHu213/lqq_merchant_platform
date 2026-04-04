package com.zbkj.admin.controller.merchant;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.MerchantServiceStaffRequest;
import com.zbkj.common.request.merchant.MerchantServiceStaffSearchRequest;
import com.zbkj.common.response.MerchantServiceStaffResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.MerchantServiceStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/merchant/service/staff")
@Api(tags = "商家端 - 服务员工管理")
public class MerchantServiceStaffController {

    @Autowired
    private MerchantServiceStaffService merchantServiceStaffService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @author zzp
     */
    @PreAuthorize("hasAuthority('merchant:service:staff:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MerchantServiceStaffResponse>> getList(@ModelAttribute @Validated MerchantServiceStaffSearchRequest request,
                                                                          @ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<MerchantServiceStaffResponse> merchantServiceStaffCommonPage = CommonPage.restPage(merchantServiceStaffService.getList(request, pageParamRequest));
        return CommonResult.success(merchantServiceStaffCommonPage);
    }

    /**
     * 新增
     * @param merchantServiceStaffRequest 新增参数
     * @author zzp
     */
    @PreAuthorize("hasAuthority('merchant:service:staff:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated MerchantServiceStaffRequest merchantServiceStaffRequest) {
        if(merchantServiceStaffService.addMerchantServiceStaff(merchantServiceStaffRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 删除
     * @param id Integer
     * @author zzp
     */
    @PreAuthorize("hasAuthority('merchant:service:staff:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable Integer id) {
        if(merchantServiceStaffService.deleteById(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改
     * @param merchantServiceStaffRequest 修改参数
     * @author zzp
     */
    @PreAuthorize("hasAuthority('merchant:service:staff:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated MerchantServiceStaffRequest merchantServiceStaffRequest) {
        if(merchantServiceStaffService.editMerchantServiceStaff(merchantServiceStaffRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询信息
     * @param id Integer
     * @author zzp
     */
    @PreAuthorize("hasAuthority('merchant:service:staff:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<MerchantServiceStaffResponse> info(@PathVariable Integer id) {
        return CommonResult.success(merchantServiceStaffService.getStaffInfo(id));
    }

    /**
     * 修改状态
     * @param id Integer
     * @author zzp
     */
    @PreAuthorize("hasAuthority('merchant:service:staff:status')")
    @ApiOperation(value = "修改员工状态")
    @RequestMapping(value = "/status/{id}", method = RequestMethod.POST)
    public CommonResult<String> status(@PathVariable Integer id) {
        if(merchantServiceStaffService.updateStaffStatus(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

}
