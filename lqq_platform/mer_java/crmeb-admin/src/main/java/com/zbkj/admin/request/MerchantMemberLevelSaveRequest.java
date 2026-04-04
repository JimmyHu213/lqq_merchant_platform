package com.zbkj.admin.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户会员等级保存请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberLevelSaveRequest", description = "商户会员等级保存请求对象")
public class MerchantMemberLevelSaveRequest implements Serializable {

    private static final long serialVersionUID = -2591205486325774521L;

    @ApiModelProperty(value = "会员等级ID，新增不填")
    private Integer id;

    @ApiModelProperty(value = "等级,编辑不填")
    private Integer level;

    @ApiModelProperty(value = "会员等级名称", required = true)
    @NotBlank(message = "会员等级名称不能为空")
    @Length(max = 10, message = "会员等级名称最多10个字符")
    private String name;

    @ApiModelProperty(value = "门槛金额", required = true)
    @NotNull(message = "门槛金额不能为空")
    private BigDecimal thresholdAmount;

    @ApiModelProperty(value = "会员权益")
    private String benefits;

    @ApiModelProperty(value = "优惠券ids")
    private String couponIds;
}
