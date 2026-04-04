package com.zbkj.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商户会员权益响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MerchantMemberBenefitsResponse", description = "商户会员权益响应对象")
public class MerchantMemberBenefitsResponse implements Serializable {

    private static final long serialVersionUID = -8427920509563254586L;

    @ApiModelProperty(value = "会员等级ID")
    private Integer id;

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

}
