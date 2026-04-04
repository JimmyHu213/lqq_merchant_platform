package com.zbkj.common.vo;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 分销基础配置Vo对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/11/26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RetailStoreBaseConfigVo", description = "分销基础配置Vo对象")
public class RetailStoreBaseConfigVo implements Serializable {

    private static final long serialVersionUID = -5457067205576628700L;

    @ApiModelProperty(value = "是否启用分销:1-启用，0-禁止")
    @NotNull(message = "是否启用分销 不能为空")
    @Range(min = 0, max = 1, message = "超出分销开关选择范围")
    private Integer retailStoreSwitch;

    @ApiModelProperty(value = "分销额度：-1-关闭，0--用户购买金额大于等于设置金额时，用户自动成为分销员")
    @NotNull(message = "分销额度 不能为空")
    @Min(value = -1, message = "分销额度,不能小于-1")
    private Integer retailStoreLine;

    @ApiModelProperty(value = "分销关系绑定:0-所有用户，1-新用户")
    @NotNull(message = "分销关系绑定 不能为空")
    @Range(min = 0, max = 1, message = "未知的分校关系绑定类型")
    private Integer retailStoreBindingType;

    @ApiModelProperty(value = "分销一级返佣比例")
    @NotNull(message = "一级返佣比例 不能为空")
    @Range(min = 0, max = 100, message = "一级返佣比例请在0-100中选择")
    private Integer retailStoreBrokerageFirstRatio;

    @ApiModelProperty(value = "分销二级返佣比例")
    @NotNull(message = "二级返佣比例 不能为空")
    @Range(min = 0, max = 100, message = "二级返佣比例在0-100中选择")
    private Integer retailStoreBrokerageSecondRatio;

    @ApiModelProperty(value = "分销佣金冻结时间")
    @NotNull(message = "分销佣金冻结时间 不能为空")
    @Min(value = 0, message = "分销佣金冻结时间最少为0天")
    private Integer retailStoreBrokerageFreezingTime;

    @ApiModelProperty(value = "分销佣金分账节点:pay:订单支付后，receipt:订单收货后，complete:订单完成后", required = true)
    @NotBlank(message = "分销佣金分账节点不能为空")
    @StringContains(limitValues = {"pay", "receipt", "complete"}, message = "未知的分销佣金分账节点")
    private String retailStoreBrokerageShareNode;

}
