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
 * @Date 2025/10/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RelatedCircleRequest", description="关联圈层请求对象")
public class RelatedCircleRequest implements Serializable {

    private static final long serialVersionUID = 5740926564895703809L;

    @ApiModelProperty(value = "圈层DIY模板ID")
    @NotNull(message = "圈层DIY模板ID 不能为空")
    private Integer id;

    @ApiModelProperty(value = "圈层ID字符串（英文逗号拼接）")
    @NotBlank(message = "请选择圈层")
    private String circleIds;
}
