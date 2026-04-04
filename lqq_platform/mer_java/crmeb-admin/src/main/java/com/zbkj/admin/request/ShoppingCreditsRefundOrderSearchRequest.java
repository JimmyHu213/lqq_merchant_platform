package com.zbkj.admin.request;

import com.zbkj.common.request.UserCommonSearchRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 购物金退款订单查询请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ShoppingCreditsRefundOrderSearchRequest", description = "购物金退款订单查询请求对象")
public class ShoppingCreditsRefundOrderSearchRequest extends UserCommonSearchRequest implements Serializable {

    private static final long serialVersionUID = 6006022397727523036L;

    @ApiModelProperty(value = "退款单号")
    private String refundOrderNo;

    @ApiModelProperty(value = "充值单号")
    private String orderNo;

    @ApiModelProperty(value = "申请时间区间")
    private String dateLimit;

    @ApiModelProperty(value = "售后状态：0:待审核 1:商家拒绝 2：退款中 3:已退款 99:全部")
    private Integer refundStatus;
}
