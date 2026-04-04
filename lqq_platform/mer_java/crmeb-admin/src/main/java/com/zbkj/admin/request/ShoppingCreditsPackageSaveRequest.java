package com.zbkj.admin.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物金套餐保存请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/4
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ShoppingCreditsPackageSaveRequest", description = "购物金套餐保存请求对象")
public class ShoppingCreditsPackageSaveRequest implements Serializable {

    private static final long serialVersionUID = 680680046459467650L;

    @ApiModelProperty(value = "套餐ID,新增不传")
    private Integer id;

    @ApiModelProperty(value = "充值金额")
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额不能小于0.01")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "赠送金额")
    @NotNull(message = "赠送金额不能为空")
    private BigDecimal giftAmount;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @ApiModelProperty(value = "显示状态:0-关闭，1-展示")
    @NotNull(message = "显示状态不能为空")
    private Integer showStatus;
}
