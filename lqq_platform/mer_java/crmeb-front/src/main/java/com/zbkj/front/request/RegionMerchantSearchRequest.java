package com.zbkj.front.request;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 圈层商户搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RegionMerchantSearchRequest", description = "圈层商户搜索请求对象")
public class RegionMerchantSearchRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = -3161227983656677633L;

    @ApiModelProperty(value = "圈层ID集合，分号分隔")
    private String regionIds;

    @ApiModelProperty(value = "首页商户数量")
    private Integer num;
}
