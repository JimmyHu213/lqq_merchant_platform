package com.zbkj.common.response.statistcs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 核心数据统计响应对象
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
@ApiModel(value="StatisticsTopResponse对象", description="核心数据统计响应对象")
public class StatisticsTopResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "指标名称")
    private String name;

    @ApiModelProperty(value = "指标数值")
    private Number value;

    @ApiModelProperty(value = "环比增长率")
    private String growthRate;

    @ApiModelProperty(value = "是否为增长（true:增长, false:下降）")
    private Boolean increased;
}
