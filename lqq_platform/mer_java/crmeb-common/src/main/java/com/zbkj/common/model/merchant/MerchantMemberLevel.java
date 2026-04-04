package com.zbkj.common.model.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商户会员等级表
 * </p>
 *
 * @author HZW
 * @since 2025-12-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_member_level")
@ApiModel(value = "MerchantMemberLevel对象", description = "商户会员等级表")
public class MerchantMemberLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员等级ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

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

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
