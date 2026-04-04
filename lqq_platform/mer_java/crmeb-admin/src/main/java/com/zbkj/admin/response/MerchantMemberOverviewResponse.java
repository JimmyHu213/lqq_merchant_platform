package com.zbkj.admin.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 商户会员概览响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberOverviewResponse", description = "商户会员概览响应对象")
public class MerchantMemberOverviewResponse implements Serializable {

    private static final long serialVersionUID = -6462502653195747593L;

    @ApiModelProperty(value = "现有会员数")
    private Integer memberNum;

    @ApiModelProperty(value = "会员等级分布")
    private Map<String, Integer> memberLevelMap;

    @ApiModelProperty(value = "会员来源渠道分布")
    private Map<String, Integer> memberSourceMap;
}
