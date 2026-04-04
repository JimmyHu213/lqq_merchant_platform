package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户用户分页响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantUserPageResponse", description = "商户用户分页响应对象")
public class MerchantUserPageResponse implements Serializable {

    private static final long serialVersionUID = 6187205286317696985L;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private Integer sex;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "会员等级")
    private Integer level;

    @ApiModelProperty(value = "会员等级名称")
    private String levelName;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giftAmount;

    @ApiModelProperty(value = "财务状态:0-冻结，1-正常")
    private Integer financialStatus;

    @ApiModelProperty(value = "创建时间")
    private Date membershipTime;

    @ApiModelProperty(value = "是否关注")
    private Integer isCollect;
}
