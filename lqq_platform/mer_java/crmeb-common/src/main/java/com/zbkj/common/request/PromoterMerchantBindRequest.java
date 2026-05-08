package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 推广员-商户绑定请求对象
 * [LQQ-迁移] 推广员代理模式
 */
@Data
@ApiModel(value = "PromoterMerchantBindRequest", description = "推广员-商户绑定请求对象")
public class PromoterMerchantBindRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "推广员用户ID", required = true)
    @NotNull(message = "推广员用户ID不能为空")
    private Integer uid;

    @ApiModelProperty(value = "代理佣金比例(%)", required = true)
    @NotNull(message = "佣金比例不能为空")
    @DecimalMin(value = "0.01", message = "佣金比例不能小于0.01%")
    @DecimalMax(value = "100.00", message = "佣金比例不能超过100%")
    private BigDecimal commissionRate;
}
