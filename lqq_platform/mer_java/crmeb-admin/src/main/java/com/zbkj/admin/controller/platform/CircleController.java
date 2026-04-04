package com.zbkj.admin.controller.platform;

import com.zbkj.common.request.circle.CircleAddRequest;
import com.zbkj.common.request.circle.CircleConjunctiveDiyRequest;
import com.zbkj.common.request.circle.CircleMerchantRequest;
import com.zbkj.common.request.circle.CircleUpdateRequest;
import com.zbkj.common.response.circle.CircleDetailResponse;
import com.zbkj.common.response.circle.CircleMerchantResponse;
import com.zbkj.common.response.circle.CircleTreeResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CircleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 圈层管理表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/circle")
@Api(tags = "圈层控制器") //配合swagger使用
public class CircleController {

    @Autowired
    private CircleService circleService;

    @PreAuthorize("hasAuthority('platform:circle:list')")
    @ApiOperation(value = "获取圈层tree结构的列表") //配合swagger使用
    @RequestMapping(value = "/list/tree", method = RequestMethod.GET)
    public CommonResult<List<CircleTreeResponse>>  getListTree() {
        return CommonResult.success(circleService.getListTree());
    }

    @PreAuthorize("hasAuthority('platform:circle:save')")
    @ApiOperation(value = "新增圈层")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated CircleAddRequest request) {
        Integer id = circleService.add(request);
        if(id > 0) {
            return CommonResult.success(id.toString());
        }
        return CommonResult.failed("圈层新增失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:delete')")
    @ApiOperation(value = "删除圈层")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if(circleService.removeCircleById(id)) {
            return CommonResult.success("圈层删除成功");
        }
        return CommonResult.failed("圈层删除失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:update')")
    @ApiOperation(value = "修改圈层")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated CircleUpdateRequest request) {
        if(circleService.updateCircle(request)) {
            return CommonResult.success("圈层修改成功");
        }
        return CommonResult.failed("圈层修改失败");

    }

    @PreAuthorize("hasAuthority('platform:circle:info')")
    @ApiOperation(value = "查询圈层详情信息")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<CircleDetailResponse> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(circleService.getCircleId(id));
    }

    @PreAuthorize("hasAuthority('platform:circle:reset')")
    @ApiOperation(value="重置圈层密码")
    @RequestMapping(value = "/reset/password/{id}", method = RequestMethod.POST)
    public CommonResult<String> resetPassword(@PathVariable(value = "id") Integer id) {
        if (circleService.resetPassword(id)) {
            return CommonResult.success("重置圈层管理员密码成功");
        }
        return CommonResult.failed("重置圈层管理员密码失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:status')")
    @ApiOperation(value="圈层状态开关")
    @RequestMapping(value = "/status/{id}", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@PathVariable(value = "id") Integer id) {
        if (circleService.updateStatus(id)) {
            return CommonResult.success("圈层状态修改成功");
        }
        return CommonResult.failed("圈层状态修改失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:show')")
    @ApiOperation(value="圈层显示开关")
    @RequestMapping(value = "/show/{id}", method = RequestMethod.POST)
    public CommonResult<String> updateShow(@PathVariable(value = "id") Integer id) {
        if (circleService.updateShow(id)) {
            return CommonResult.success("圈层显示开关修改成功");
        }
        return CommonResult.failed("圈层显示开关修改失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:bind')")
    @ApiOperation(value="圈层关联商户")
    @RequestMapping(value = "/bind/merchant", method = RequestMethod.POST)
    public CommonResult<String> bindMerchant(@RequestBody @Validated CircleMerchantRequest request) {
        if (circleService.bindMerchant(request)) {
            return CommonResult.success("圈层关联商户成功");
        }
        return CommonResult.failed("圈层关联商户失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:unbind')")
    @ApiOperation(value="圈层解绑商户")
    @RequestMapping(value = "/remove/merchant", method = RequestMethod.POST)
    public CommonResult<String> removeMerchant(@RequestBody @Validated CircleMerchantRequest request) {
        if (circleService.removeMerchant(request)) {
            return CommonResult.success("圈层解绑商户成功");
        }
        return CommonResult.failed("圈层解绑商户失败");
    }

    @PreAuthorize("hasAuthority('platform:circle:merchant')")
    @ApiOperation(value="查询圈层未关联商户-接口待处理")
    @RequestMapping(value = "/merchant/{id}", method = RequestMethod.GET)
    public CommonResult<List<CircleMerchantResponse>> bindMerchant(@ApiParam(value = "圈层ID（0表示未归属圈层）", required = true)
                                                                      @PathVariable("id") Integer id) {
        return CommonResult.success(circleService.getMerchantsByCircleId(id));
    }

    @PreAuthorize("hasAuthority('platform:circle:conjunctive:diy')")
    @ApiOperation(value="圈层关联DIY模板")
    @RequestMapping(value = "/conjunctive/diy", method = RequestMethod.POST)
    public CommonResult<String> conjunctiveDiy(@RequestBody @Validated CircleConjunctiveDiyRequest request) {
        if (circleService.conjunctiveDiy(request)) {
            return CommonResult.success("关联成功");
        }
        return CommonResult.failed("关联失败");
    }
}




