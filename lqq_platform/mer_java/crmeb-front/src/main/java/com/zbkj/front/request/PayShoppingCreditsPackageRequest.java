package com.zbkj.front.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 购买购物金套餐请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/4
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PayShoppingCreditsPackageRequest", description = "购买购物金套餐请求对象")
public class PayShoppingCreditsPackageRequest implements Serializable {

    private static final long serialVersionUID = 7092901997692156335L;

    @ApiModelProperty(value = "购物金套餐ID")
    @NotNull(message = "套餐ID不能为空")
    private Integer packageId;

    @ApiModelProperty(value = "支付方式:weixin,alipay", required = true)
    @NotBlank(message = "支付方式不能为空")
    @StringContains(limitValues = {"weixin", "alipay"}, message = "未知的支付方式")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App", required = true)
    @NotBlank(message = "支付渠道不能为空")
    @StringContains(limitValues = {"public", "mini", "h5", "wechatIos", "wechatAndroid", "alipay", "alipayApp"}, message = "未知的支付渠道")
    private String payChannel;
}
