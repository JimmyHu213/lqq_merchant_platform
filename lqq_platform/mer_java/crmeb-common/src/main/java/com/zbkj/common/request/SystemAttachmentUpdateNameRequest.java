package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 附件编辑名称请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/10/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SystemAttachmentUpdateNameRequest", description = "附件编辑名称请求对象")
public class SystemAttachmentUpdateNameRequest implements Serializable {

    private static final long serialVersionUID = 4242326338592103879L;

    @ApiModelProperty(value = "素材ID")
    @NotNull(message = "请选择素材")
    private Integer attId;

    @ApiModelProperty(value = "素材别名")
    @NotBlank(message = "请填写素材名称")
    private String alias;
}
