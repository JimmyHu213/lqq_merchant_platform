package com.zbkj.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 移动端首页商品组件列表请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/10/31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "IndexProductComponentRequest", description = "移动端首页商品组件列表请求对象")
public class IndexProductComponentRequest implements Serializable {

    private static final long serialVersionUID = -645230228431887863L;

    @ApiModelProperty(value = "分类id，多个分类id用逗号隔开")
    private String cids;

    @ApiModelProperty(value = "价格排序", allowableValues = "range[asc,desc]")
    private String priceOrder;

    @ApiModelProperty(value = "销量排序", allowableValues = "range[asc,desc]")
    private String salesOrder;

    @ApiModelProperty(value = "品牌id,多个品牌id用逗号隔开")
    private String brandIds;

    @ApiModelProperty(value = "商户id,多个商户id用逗号分隔")
    private String merIds;

    @ApiModelProperty(value = "圈层ID,多个圈层id用逗号分隔")
    private String circleIds;

    @ApiModelProperty(value = "首页商品数量")
    private Integer num;

    @ApiModelProperty(value = "商品id,多个商品id用逗号分隔")
    private String proIds;
}
