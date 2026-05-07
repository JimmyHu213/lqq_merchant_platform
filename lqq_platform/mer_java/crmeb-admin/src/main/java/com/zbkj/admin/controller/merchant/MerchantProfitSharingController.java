package com.zbkj.admin.controller.merchant;

import com.zbkj.common.model.order.WechatProfitSharingRecord;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.WechatProfitSharingRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [LQQ-迁移] 商户端微信分账记录控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/profit-sharing")
@Api(tags = "[LQQ] 商户端 - 微信分账记录")
public class MerchantProfitSharingController {

    @Autowired
    private WechatProfitSharingRecordService recordService;

    @PreAuthorize("hasAuthority('merchant:profit:sharing:list')")
    @ApiOperation(value = "本商户分账记录列表")
    @ApiImplicitParam(name = "date", value = "日期(yyyy-MM-dd)", dataType = "String")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<WechatProfitSharingRecord>> list(
            @RequestParam(required = false) String date) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        return CommonResult.success(recordService.getByMerId(merId, date));
    }

    @PreAuthorize("hasAuthority('merchant:profit:sharing:detail')")
    @ApiOperation(value = "分账记录详情 — 按订单号查询")
    @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, dataType = "String")
    @RequestMapping(value = "/detail/{orderNo}", method = RequestMethod.GET)
    public CommonResult<List<WechatProfitSharingRecord>> detail(@PathVariable String orderNo) {
        return CommonResult.success(recordService.getByOrderNo(orderNo));
    }
}
