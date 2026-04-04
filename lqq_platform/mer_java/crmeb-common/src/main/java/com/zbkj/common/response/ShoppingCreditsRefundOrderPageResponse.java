package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物金腿跨订单分页列表响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/9
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ShoppingCreditsRefundOrderPageResponse", description = "购物金腿跨订单分页列表响应对象")
public class ShoppingCreditsRefundOrderPageResponse implements Serializable {

    private static final long serialVersionUID = 9039908030516767110L;

    @ApiModelProperty(value = "退款订单号")
    private String refundOrderNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "售后状态：0:待审核 1:商家拒绝 2：退款中 3:已退款 4:用户撤销")
    private Integer refundStatus;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty(value = "退赠送金额")
    private BigDecimal refundGiftAmount;

    @ApiModelProperty(value = "创建时间/申请时间")
    private Date createTime;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;
}

