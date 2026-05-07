package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用操作结果响应对象
 * [LQQ-迁移] 用于 Knife4j 文档展示，替代 CommonResult<String> 以生成响应示例
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "OperationResponse", description = "操作结果响应对象")
public class OperationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作结果描述")
    private String message;
}
