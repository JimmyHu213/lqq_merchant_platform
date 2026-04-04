package com.zbkj.admin.response;

import com.zbkj.common.vo.CouponSimpleVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商户会员等级列表响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberLevelSaveResponse", description = "商户会员等级列表响应对象")
public class MerchantMemberLevelSaveResponse implements Serializable {

    private static final long serialVersionUID = -2591205486325774521L;

    @ApiModelProperty(value = "会员等级ID，新增不填")
    private Integer id;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "会员名称")
    private String name;

    @ApiModelProperty(value = "门槛金额")
    private BigDecimal thresholdAmount;

    @ApiModelProperty(value = "会员权益")
    private String benefits;

    @ApiModelProperty(value = "优惠券ids")
    private String couponIds;

    @ApiModelProperty(value = "优惠券ids")
    private List<MerMemCouponResponse> couponList;

    @ApiModelProperty(value = "会员人数")
    private Integer num;
}
