package com.zbkj.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 首页商户请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/10/31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IndexMerchantRequest", description = "首页商户请求对象")
public class IndexMerchantRequest implements Serializable {

    private static final long serialVersionUID = -3842425972360781386L;

    @ApiModelProperty(value = "圈层ID,多个圈层id用逗号分隔")
    private String circleIds;

    @ApiModelProperty(value = "商户id,多个商户id用逗号分隔")
    private String merIds;

    @ApiModelProperty(value = "首页商户数量")
    private Integer num;
}
