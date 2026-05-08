package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.order.WechatProfitSharingRecord;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.WechatProfitSharingRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [LQQ-迁移] 平台端微信分账管理控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/profit-sharing")
@Api(tags = "[LQQ] 平台端 - 微信分账管理")
public class PlatformProfitSharingController {

    @Autowired
    private WechatProfitSharingRecordService recordService;

    @PreAuthorize("hasAuthority('platform:profit:sharing:list')")
    @ApiOperation(value = "分账记录列表 — 按商户筛选")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merId", value = "商户ID(0=全部)", dataType = "int"),
            @ApiImplicitParam(name = "date", value = "日期(yyyy-MM-dd)", dataType = "String")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<WechatProfitSharingRecord>> list(
            @RequestParam(defaultValue = "0") Integer merId,
            @RequestParam(required = false) String date) {
        return CommonResult.success(recordService.getByMerId(merId, date));
    }

    @PreAuthorize("hasAuthority('platform:profit:sharing:detail')")
    @ApiOperation(value = "分账记录详情 — 按订单号查询")
    @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, dataType = "String")
    @RequestMapping(value = "/detail/{orderNo}", method = RequestMethod.GET)
    public CommonResult<List<WechatProfitSharingRecord>> detail(@PathVariable String orderNo) {
        return CommonResult.success(recordService.getByOrderNo(orderNo));
    }
}
