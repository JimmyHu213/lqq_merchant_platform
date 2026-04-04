package com.zbkj.admin.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物金退款订单详情响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/8
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ShoppingCreditsRefundOrderInfoResponse", description = "购物金退款订单详情响应对象")
public class ShoppingCreditsRefundOrderInfoResponse implements Serializable {

    private static final long serialVersionUID = -5306994730485834576L;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "退款订单号")
    private String refundOrderNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty(value = "退赠送金额")
    private BigDecimal refundGiftAmount;

    @ApiModelProperty(value = "退款原因")
    private String refundReason;

    @ApiModelProperty(value = "创建时间/申请时间")
    private Date createTime;

    @ApiModelProperty(value = "售后状态：0:待审核 1:商家拒绝 2：退款中 3:已退款 4:用户撤销")
    private Integer refundStatus;

    @ApiModelProperty(value = "拒绝退款说明")
    private String refusingRefundReason;

    @ApiModelProperty(value = "商户备注")
    private String merRemark;

    @ApiModelProperty(value = "审核员id")
    private Integer auditId;

    @ApiModelProperty(value = "审核员名称")
    private String auditName;

    @ApiModelProperty(value = "0-系统，1-平台管理员，2-商户管理员，3-移动端商户管理员")
    private Integer auditType;

    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
}
