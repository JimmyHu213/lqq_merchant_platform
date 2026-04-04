package com.zbkj.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销员导出DTO对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RetailStorePeopleExportDto", description = "分销员导出DTO对象")
public class RetailStorePeopleExportDto {


    @ApiModelProperty(value = "用户id")
    private Integer id;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "佣金金额,未提现金额")
    private BigDecimal brokeragePrice;

    @ApiModelProperty(value = "推广人id")
    private Integer spreadUid;

    @ApiModelProperty(value = "上级推广员名称")
    private String spreadNickname;

    @ApiModelProperty(value = "用户购买次数")
    private Integer payCount;

    @ApiModelProperty(value = "推广用户数")
    private Integer spreadCount;

    @ApiModelProperty(value = "推广订单数")
    private Integer spreadOrderNum;

    @ApiModelProperty(value = "推广订单额")
    private BigDecimal spreadOrderTotalPrice;

    @ApiModelProperty(value = "佣金总金额")
    private BigDecimal totalBrokeragePrice;

    @ApiModelProperty(value = "推广用户数量")
    private Integer spreadPeopleCount;

    @ApiModelProperty(value = "已提现金额")
    private BigDecimal extractCountPrice;

    @ApiModelProperty(value = "已提现次数")
    private Integer extractCountNum;

    @ApiModelProperty(value = "冻结佣金")
    private BigDecimal freezeBrokeragePrice;

    @ApiModelProperty(value = "成为分销员时间")
    private Date promoterTime;
}
