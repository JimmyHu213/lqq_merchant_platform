package com.zbkj.front.request;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 圈层商品搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RegionProductSearchRequest", description = "圈层商品搜索请求对象")
public class CircleProductSearchRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = -7663083710002417773L;

    @ApiModelProperty(value = "圈层ID集合，分号分隔")
    private String circleIds;

}
