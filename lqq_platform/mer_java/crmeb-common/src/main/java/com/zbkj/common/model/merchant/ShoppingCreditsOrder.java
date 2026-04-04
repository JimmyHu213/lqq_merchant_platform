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
 * 购物金订单
 * </p>
 *
 * @author HZW
 * @since 2025-12-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_shopping_credits_order")
@ApiModel(value = "ShoppingCreditsOrder对象", description = "购物金订单")
public class ShoppingCreditsOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "充值用户UID")
    private Integer uid;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giftAmount;

    @ApiModelProperty(value = "支付方式:weixin,alipay")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付包，alipayApp-支付宝App")
    private String payChannel;

    @ApiModelProperty(value = "是否支付")
    private Boolean paid;

    @ApiModelProperty(value = "充值支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付服务方订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "充值时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否微信小程序发货")
    private Boolean isWechatShipping;

    @ApiModelProperty(value = "退款状态：0 未退款 1 申请中 2 已退款")
    private Integer refundStatus;
}
