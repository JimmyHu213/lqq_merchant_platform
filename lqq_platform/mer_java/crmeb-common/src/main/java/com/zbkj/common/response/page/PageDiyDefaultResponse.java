package com.zbkj.common.response.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PageDiyDefaultResponse 对象", description="商城首页封装数据")
public class PageDiyDefaultResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "首页Id")
    private Integer id;

    @ApiModelProperty(value = "首页类型：homePage-商城diy,regionHome-圈层diy")
    private String type;
}
