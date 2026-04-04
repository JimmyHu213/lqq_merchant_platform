package com.zbkj.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 加入商户会员响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BecomeMerchantMemberResponse", description = "加入商户会员响应对象")
public class BecomeMerchantMemberResponse implements Serializable {

    private static final long serialVersionUID = -5077357278403010169L;

    @ApiModelProperty(value = "优惠券列表")
    private List<BecomeMerchantMemberCouponResponse> couponList;
}
