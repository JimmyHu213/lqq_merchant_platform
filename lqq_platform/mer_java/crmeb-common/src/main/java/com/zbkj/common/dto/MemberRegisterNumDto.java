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
@ApiModel(value = "MemberRegisterNumDto", description = "商户会员来源渠道数量dto对象")
public class MemberRegisterNumDto {

    @ApiModelProperty(value = "注册类型：wechat-公众号，routine-小程序，h5-H5,iosWx-微信ios，androidWx-微信安卓，ios-ios")
    private String registerType;

    @ApiModelProperty("商户会员数量")
    private Integer num;

}
