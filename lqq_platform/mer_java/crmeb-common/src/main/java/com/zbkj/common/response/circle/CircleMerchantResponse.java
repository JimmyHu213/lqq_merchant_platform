package com.zbkj.common.response.circle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CircleMerchantResponse对象", description="平台端圈层归属商户响应对象")
public class CircleMerchantResponse implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商户ID")
    private Integer id;

    @ApiModelProperty(value = "商户名称")
    private String name;

    @ApiModelProperty(value = "商户姓名")
    private String realName;

    @ApiModelProperty(value = "商户手机号")
    private String phone;

    @ApiModelProperty(value = "商户详细地址")
    private String addressDetail;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "经度")
    private String longitude;

}
