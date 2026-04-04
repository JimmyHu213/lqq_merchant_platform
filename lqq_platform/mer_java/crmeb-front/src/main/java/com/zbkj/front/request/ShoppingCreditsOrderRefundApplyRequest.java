package com.zbkj.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 购物金订单退款申请请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/8
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ShoppingCreditsOrderRefundApplyRequest", description = "购物金订单退款申请请求对象")
public class ShoppingCreditsOrderRefundApplyRequest implements Serializable {

    private static final long serialVersionUID = -7071679619295872447L;

    @ApiModelProperty(value = "订单号", required = true)
    @NotEmpty(message = "订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "退款原因", required = true)
    @NotEmpty(message = "退款原因必须填写")
    @Length(max = 255, message = "退款原因不能超过255个字符")
    private String refundReason;
}
