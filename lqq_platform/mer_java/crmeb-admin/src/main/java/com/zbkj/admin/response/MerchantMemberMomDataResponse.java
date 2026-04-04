package com.zbkj.admin.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户会员环比数据响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberMomDataResponse", description = "商户会员环比数据响应对象")
public class MerchantMemberMomDataResponse implements Serializable {

    private static final long serialVersionUID = -6462502653195747593L;

    @ApiModelProperty(value = "新增会员数")
    private Integer newNum;

    @ApiModelProperty(value = "新增会员数环比")
    private String newNumMom;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "充值金额环比")
    private String rechargeAmountMom;

    @ApiModelProperty(value = "充值订单数")
    private Integer rechargeOrderNum;

    @ApiModelProperty(value = "充值订单数环比")
    private String rechargeOrderNumMom;

    @ApiModelProperty(value = "消费金额")
    private BigDecimal consumeAmount;

    @ApiModelProperty(value = "消费金额环比")
    private String consumeAmountMom;

    @ApiModelProperty(value = "消费订单数")
    private Integer consumeOrderNum;

    @ApiModelProperty(value = "消费订单数环比")
    private String consumeOrderNumMom;

    @ApiModelProperty(value = "消费会员人数")
    private Integer consumeMemberNum;

    @ApiModelProperty(value = "消费会员人数环比")
    private String consumeMemberNumMom;

    @ApiModelProperty(value = "活跃会员人数")
    private Integer activeMemberAmount;

    @ApiModelProperty(value = "活跃会员人数环比")
    private String activeMemberAmountMom;
}
