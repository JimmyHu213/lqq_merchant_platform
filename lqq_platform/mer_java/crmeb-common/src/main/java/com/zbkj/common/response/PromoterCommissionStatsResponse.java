package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 推广员佣金统计响应对象
 * [LQQ-迁移] 推广员代理模式
 */
@Data
@ApiModel(value = "PromoterCommissionStatsResponse", description = "推广员佣金统计响应对象")
public class PromoterCommissionStatsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "代理佣金总额")
    private BigDecimal agentTotal;

    @ApiModelProperty(value = "裂变佣金总额")
    private BigDecimal referralTotal;

    @ApiModelProperty(value = "佣金总计")
    private BigDecimal totalCommission;

    @ApiModelProperty(value = "代理商户数")
    private Integer merchantCount;
}
