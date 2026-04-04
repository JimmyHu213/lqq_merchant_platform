package com.zbkj.common.model.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 购物金退款单表
 * </p>
 *
 * @author HZW
 * @since 2026-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_shopping_credits_refund_order")
@ApiModel(value = "ShoppingCreditsRefundOrder对象", description = "购物金退款单表")
public class ShoppingCreditsRefundOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "退款订单号")
    private String refundOrderNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "退款原因")
    private String refundReason;

    @ApiModelProperty(value = "是否整单退款")
    private Boolean isAll;

    @ApiModelProperty(value = "售后状态：0:待审核 1:商家拒绝 2：退款中 3:已退款 4:用户撤销")
    private Integer refundStatus;

    @ApiModelProperty(value = "拒绝退款说明")
    private String refusingRefundReason;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty(value = "退赠送金额")
    private BigDecimal refundGiftAmount;

    @ApiModelProperty(value = "审核员id")
    private Integer auditId;

    @ApiModelProperty(value = "0-系统，1-平台管理员，2-商户管理员，3-移动端商户管理员")
    private Integer auditType;

    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    @ApiModelProperty(value = "退款时间")
    private Date refundTime;

    @ApiModelProperty(value = "退款渠道类型:weixin,alipay")
    private String refundPayType;

    @ApiModelProperty(value = "商户备注")
    private String merRemark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
