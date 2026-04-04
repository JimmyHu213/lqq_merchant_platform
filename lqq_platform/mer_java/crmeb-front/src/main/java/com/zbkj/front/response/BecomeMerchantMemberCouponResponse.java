package com.zbkj.front.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 加入商户会员优惠券响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BecomeMerchantMemberCouponResponse", description = "加入商户会员优惠券响应对象")
public class BecomeMerchantMemberCouponResponse implements Serializable {

    private static final long serialVersionUID = 4447969690486735246L;

    @ApiModelProperty(value = "优惠券表ID")
    private Integer id;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠金额")
    private Long money;

    @ApiModelProperty(value = "最低消费，0代表不限制")
    private Long minPrice;

    @ApiModelProperty(value = "可使用时间范围 开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date useStartTime;

    @ApiModelProperty(value = "可使用时间范围 结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date useEndTime;
}
