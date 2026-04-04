package com.zbkj.common.response.statistcs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单折线趋势图统计响应对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="ProductStatisticsChartResponse对象", description="商品折线趋势图统计响应对象")
public class OrderStatisticsChartResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "订单数量")
    private Integer orderVolume = 0;

    @ApiModelProperty(value = "退款数量")
    private Integer refundVolume = 0;
}
