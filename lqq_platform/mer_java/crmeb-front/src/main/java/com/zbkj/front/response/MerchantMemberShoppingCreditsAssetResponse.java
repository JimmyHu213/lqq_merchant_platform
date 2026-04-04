package com.zbkj.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户会员购物金账户响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberShoppingCreditsAssetResponse", description = "商户会员购物金账户响应对象")
public class MerchantMemberShoppingCreditsAssetResponse implements Serializable {

    private static final long serialVersionUID = 6117136399552349847L;

    @ApiModelProperty(value = "购物金余额")
    private BigDecimal shoppingCreditsBalance = new BigDecimal("0.00");;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeAmount = new BigDecimal("0.00");

    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giftAmount = new BigDecimal("0.00");

    @ApiModelProperty(value = "累计充值金额")
    private BigDecimal rechargeTotalAmount = new BigDecimal("0.00");

    @ApiModelProperty(value = "累计赠送金额")
    private BigDecimal giftTotalAmount = new BigDecimal("0.00");

    @ApiModelProperty(value = "累计消费金额")
    private BigDecimal consumeTotalAmount = new BigDecimal("0.00");
}
