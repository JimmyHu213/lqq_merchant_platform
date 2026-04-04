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
 * 购物金订单分页列表响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/9
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ShoppingCreditsOrderPageResponse", description = "购物金订单分页列表响应对象")
public class ShoppingCreditsOrderPageResponse implements Serializable {

    private static final long serialVersionUID = 4587693718596341021L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giftAmount;

    @ApiModelProperty(value = "支付方式:weixin,alipay")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付包，alipayApp-支付宝App")
    private String payChannel;

    @ApiModelProperty(value = "充值支付时间")
    private Date payTime;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;
}
