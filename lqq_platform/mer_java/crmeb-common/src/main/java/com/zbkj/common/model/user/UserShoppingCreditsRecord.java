package com.zbkj.common.model.user;

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
 * 用户购物金记录表
 * </p>
 *
 * @author HZW
 * @since 2026-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_shopping_credits_record")
@ApiModel(value = "UserShoppingCreditsRecord对象", description = "用户购物金记录表")
public class UserShoppingCreditsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "关联单号（订单号、充值单号、退款单号）")
    private String linkNo;

    @ApiModelProperty(value = "关联类型（order,recharge,refund）")
    private String linkType;

    @ApiModelProperty(value = "类型：1-增加，2-扣减")
    private Integer type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giftAmount;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
