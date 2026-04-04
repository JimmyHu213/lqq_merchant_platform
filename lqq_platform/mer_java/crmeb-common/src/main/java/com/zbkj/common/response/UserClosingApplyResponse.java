package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户结算申请响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserClosingApplyResponse", description = "用户结算申请响应对象")
public class UserClosingApplyResponse implements Serializable {

    private static final long serialVersionUID = -7910434265406034526L;

    @ApiModelProperty(value = "申请结果")
    private Boolean applyResult;

    @ApiModelProperty(value = "微信商户号")
    private String mchId;

    @ApiModelProperty(value = "微信AppID")
    private String appId;

    @ApiModelProperty(value = "package信息,用于唤起用户确认收款页面")
    private String packageInfo;

    @ApiModelProperty(value = "用户结算单号")
    private String orderNo;

}
