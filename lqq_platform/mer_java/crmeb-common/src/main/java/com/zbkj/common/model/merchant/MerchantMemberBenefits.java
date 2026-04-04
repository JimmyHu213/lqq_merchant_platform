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
import java.util.Date;

/**
 * <p>
 * 商户会员权益表
 * </p>
 *
 * @author HZW
 * @since 2025-12-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_merchant_member_benefits")
@ApiModel(value = "MerchantMemberBenefits对象", description = "商户会员权益表")
public class MerchantMemberBenefits implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员等级ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "权益名称")
    private String name;

    @ApiModelProperty(value = "选中图标")
    private String selectedIcon;

    @ApiModelProperty(value = "未选中图标")
    private String unselectedIcon;

    @ApiModelProperty(value = "跳转链接")
    private String link;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否能够删除")
    private Boolean canDel;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
