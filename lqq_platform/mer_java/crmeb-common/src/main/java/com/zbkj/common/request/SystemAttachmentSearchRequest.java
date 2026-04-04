package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 附件搜索请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/10/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SystemAttachmentSearchRequest", description = "附件搜索请求对象")
public class SystemAttachmentSearchRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = 4242326338597003879L;

    @ApiModelProperty(value = "素材分类ID,0-全部")
    private Integer pid;

    @ApiModelProperty(value = "素材类型:image-图片，video-视频")
    private String type;

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;
}
