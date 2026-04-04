package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 圈层DIY模板请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/10/31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FrontPageDiyRequest", description = "移动端DIY模板请求对象")
public class FrontPageDiyRequest implements Serializable {

    private static final long serialVersionUID = -4816789566340676153L;

    @ApiModelProperty(value = "模板ID，0-返回商城首页")
    private Integer id;

    @ApiModelProperty(value = "圈层ID集合，分号分隔")
    private String circleIds;
}
