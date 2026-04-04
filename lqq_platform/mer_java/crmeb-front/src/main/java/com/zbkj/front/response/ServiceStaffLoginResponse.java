package com.zbkj.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 服务员工登录信息响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ServiceStaffLoginResponse", description = "服务员工登录信息响应对象")
public class ServiceStaffLoginResponse implements Serializable {

    private static final long serialVersionUID = -4995769946640981254L;

    @ApiModelProperty(value = "员工ID")
    private Integer id;

    @ApiModelProperty(value = "员工名称")
    private String name;

    @ApiModelProperty(value = "员工证件照")
    private String idPhoto;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "待服务工单数")
    private Integer awaitWorkNum;

    @ApiModelProperty(value = "服务中工单数")
    private Integer workingNum;

    @ApiModelProperty(value = "已服务工单数")
    private Integer workedNum;

    @ApiModelProperty(value = "用户登录密钥")
    private String token;

}
