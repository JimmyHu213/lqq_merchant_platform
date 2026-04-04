package com.zbkj.front.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户会员用户响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberUserResponse", description = "商户会员用户响应对象")
public class MerchantMemberUserResponse implements Serializable {

    private static final long serialVersionUID = 6117136399552349847L;

    @ApiModelProperty(value = "是否会员")
    private Boolean isMerchantMember;

    @ApiModelProperty(value = "会员等级")
    private Integer level;

    @ApiModelProperty(value = "购物金余额")
    private BigDecimal shoppingCreditsBalance = new BigDecimal("0.00");;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeAmount = new BigDecimal("0.00");

    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giftAmount = new BigDecimal("0.00");;

    @ApiModelProperty(value = "会员名称")
    private String levelName;

    @ApiModelProperty(value = "会员权益")
    private String benefits;
}
