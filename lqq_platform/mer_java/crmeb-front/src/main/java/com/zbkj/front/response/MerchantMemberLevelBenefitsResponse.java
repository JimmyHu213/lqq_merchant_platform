package com.zbkj.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商户会员等级权益响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberLevelBenefitsResponse", description = "商户会员等级权益响应对象")
public class MerchantMemberLevelBenefitsResponse implements Serializable {

    private static final long serialVersionUID = -8427920509563254586L;

    @ApiModelProperty(value = "会员等级ID")
    private Integer id;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "会员名称")
    private String name;

    @ApiModelProperty(value = "门槛金额")
    private BigDecimal thresholdAmount;

    @ApiModelProperty(value = "会员权益列表")
    private List<MerchantMemberBenefitsResponse> benefitsList;


}
