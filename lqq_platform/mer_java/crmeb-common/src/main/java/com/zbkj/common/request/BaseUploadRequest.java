package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName BaseUploadRequest
 * @Description base64图片上传请求对象
 * @Author HZW
 * @Date 2023/5/8 16:26
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BaseUploadRequest对象", description = "base64图片上传请求对象")
public class BaseUploadRequest implements Serializable {

    private static final long serialVersionUID = -7795148477030414962L;

    @ApiModelProperty(value = "base64地址", required = true)
    @NotBlank(message = "请填写base64地址")
    private String base64Url;
}
