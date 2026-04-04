package com.zbkj.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 圈层首页商品请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CircleProductHomeRequest", description = "圈层首页商品请求对象")
public class CircleProductHomeRequest implements Serializable {

    private static final long serialVersionUID = 7329593616335491021L;

    @ApiModelProperty(value = "圈层ID集合，分号分隔")
    private String circleIds;

    @ApiModelProperty(value = "首页商品数量")
    private Integer num;
}
