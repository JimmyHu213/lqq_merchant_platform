package com.zbkj.admin.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户会员消费趋势数据响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberConsumeTrendDataResponse", description = "商户会员消费趋势数据响应对象")
public class MerchantMemberConsumeTrendDataResponse implements Serializable {

    private static final long serialVersionUID = -6462502653225747593L;

    @ApiModelProperty(value = "日期：月.日")
    private String date;

    @ApiModelProperty(value = "下单数量")
    private Integer orderNum;

    @ApiModelProperty(value = "消费金额")
    private BigDecimal consumeAmount;

}
