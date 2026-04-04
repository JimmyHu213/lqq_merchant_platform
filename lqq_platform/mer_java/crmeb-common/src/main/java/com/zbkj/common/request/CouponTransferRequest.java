package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 优惠券转赠请求对象
 * [LQQ-迁移] 优惠券转赠功能
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CouponTransferRequest", description = "优惠券转赠请求对象")
public class CouponTransferRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户优惠券记录ID", required = true)
    @NotNull(message = "请选择要转赠的优惠券")
    private Integer couponUserId;

    @ApiModelProperty(value = "接收人用户ID", required = true)
    @NotNull(message = "请指定接收人")
    private Integer recipientUid;
}
