package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="IdPidMapping对象", description="id映射对象")
public class IdPidMapping {

    @ApiModelProperty(value = "圈层id")
    private Integer id;

    @ApiModelProperty(value = "圈层父id")
    private Integer pid;

}
