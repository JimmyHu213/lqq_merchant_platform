package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 公共IDS请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/01/04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommonIdsRequest", description = "公共IDS请求对象")
public class CommonIdsRequest implements Serializable {

    private static final long serialVersionUID = 758614615104411511L;

    @ApiModelProperty(value = "ID集合,英文逗号分隔")
    @NotBlank(message = "请传入ID")
    private String ids;

}
