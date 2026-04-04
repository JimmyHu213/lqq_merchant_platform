package com.zbkj.admin.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 商户会员优惠券响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerMemCouponResponse", description = "商户会员优惠券响应对象")
public class MerMemCouponResponse implements Serializable {

    private static final long serialVersionUID = 266586306237838105L;

    @ApiModelProperty(value = "优惠券id")
    private Integer id;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠金额")
    private Long money;

    @ApiModelProperty(value = "最低消费，0代表不限制")
    private Long minPrice;

    @ApiModelProperty(value = "是否固定使用时间, 默认0-否，1-使用固定时间")
    private Boolean isFixedTime;

    @ApiModelProperty(value = "可使用时间范围 开始时间")
    private Date useStartTime;

    @ApiModelProperty(value = "可使用时间范围 结束时间")
    private Date useEndTime;

    @ApiModelProperty(value = "天数")
    private Integer day;
}
