package com.zbkj.common.request.circle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/10/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CircleConjunctiveDiyRequest", description = "圈层关联DIY模板请求对象")
public class CircleConjunctiveDiyRequest implements Serializable {

    private static final long serialVersionUID = -6233153549792611877L;

    @ApiModelProperty(value = "圈层ID集合，逗号分隔", required = true)
    @NotBlank(message = "请选择圈层")
    private String ids;

    @ApiModelProperty(value = "DIY模板ID", required = true)
    @NotNull(message = "请选择圈层")
    private Integer diyId;
}
