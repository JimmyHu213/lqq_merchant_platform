package com.zbkj.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商户会员等级会员数量dto对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LevelMemberNumDto", description = "商户会员等级会员数量dto对象")
public class LevelMemberNumDto {

    @ApiModelProperty("商户会员等级")
    private Integer level;

    @ApiModelProperty("商户会员等级名称")
    private String name;

    @ApiModelProperty("商户会员数量")
    private Integer num;

}
