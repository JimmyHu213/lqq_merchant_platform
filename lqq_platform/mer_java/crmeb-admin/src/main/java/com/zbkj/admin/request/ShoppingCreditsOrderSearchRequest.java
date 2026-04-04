package com.zbkj.admin.request;

import com.zbkj.common.annotation.StringContains;
import com.zbkj.common.request.UserCommonSearchRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 购物金订单查询请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ShoppingCreditsOrderSearchRequest", description = "购物金订单查询请求对象")
public class ShoppingCreditsOrderSearchRequest extends UserCommonSearchRequest implements Serializable {

    private static final long serialVersionUID = -370434309955711167L;

    @ApiModelProperty(value = "充值单号")
    private String orderNo;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;

    @ApiModelProperty(value = "支付方式：weixin,alipay")
    @StringContains(limitValues = {"weixin", "alipay"}, message = "未知的支付方式")
    private String payType;
}
