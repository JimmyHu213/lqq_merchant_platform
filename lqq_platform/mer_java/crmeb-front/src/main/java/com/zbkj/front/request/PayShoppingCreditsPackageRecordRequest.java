package com.zbkj.front.request;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 购买购物金套餐记录请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/4
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PayShoppingCreditsPackageRecordRequest", description = "购买购物金套餐记录请求对象")
public class PayShoppingCreditsPackageRecordRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = 7092901997692156335L;

    @ApiModelProperty(value = "商户ID")
    @NotNull(message = "商户ID不能为空")
    private Integer merId;
}
