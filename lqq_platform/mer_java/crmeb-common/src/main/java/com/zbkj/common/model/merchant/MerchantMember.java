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
 * 商户会员表
 * </p>
 *
 * @author HZW
 * @since 2025-12-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_member")
@ApiModel(value = "MerchantMember对象", description = "商户会员表")
public class MerchantMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "会员等级")
    private Integer level;

    @ApiModelProperty(value = "财务状态:0-冻结，1-正常")
    private Integer financialStatus;

    @ApiModelProperty(value = "是否注销")
    private Boolean isLogoff;

    @ApiModelProperty(value = "注销时间")
    private Date logoffTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
