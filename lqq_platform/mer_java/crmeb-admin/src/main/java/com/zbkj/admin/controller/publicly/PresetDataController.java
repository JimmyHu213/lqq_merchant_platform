package com.zbkj.admin.controller.publicly;

import com.zbkj.admin.service.PresetDataProcessingService;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 预置数据控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/2
 */
@Slf4j
@RestController
@RequestMapping("api/publicly/preset")
@Api(tags = "预置数据控制器")
public class PresetDataController {

    @Autowired
    private PresetDataProcessingService presetDataProcessingService;

    @ApiOperation(value = "商户会员权益预置数据处理")
    @RequestMapping(value = "/member/benefits/processing", method = RequestMethod.POST)
    public CommonResult<String> MemberBenefitsProcessing() {
        if (presetDataProcessingService.MemberBenefitsPresetDataProcessing()) {
            return CommonResult.success("数据处理成功");
        }
        return CommonResult.failed("数据处理失败");
    }

    @ApiOperation(value = "商户底部导航预置数据处理")
    @RequestMapping(value = "/member/bottom/navigation/processing", method = RequestMethod.POST)
    public CommonResult<String> MemberBottomNavigationProcessing() {
        if (presetDataProcessingService.MemberBottomNavigationProcessing()) {
            return CommonResult.success("数据处理成功");
        }
        return CommonResult.failed("数据处理失败");
    }

}
