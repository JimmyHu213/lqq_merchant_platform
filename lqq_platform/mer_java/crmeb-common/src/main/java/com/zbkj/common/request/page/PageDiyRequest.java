package com.zbkj.common.request.page;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 首页商户请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/10/31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PageDiyRequest", description = "DIY模板请求对象")
public class PageDiyRequest {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "页面名称")
    private String name;

    @ApiModelProperty(value = "网站标题")
    private String title;

    @ApiModelProperty(value = "封面图")
    private String coverImage;

    @ApiModelProperty(value = "页面数据")
    private JSONObject value;

    @ApiModelProperty(value = "显示首页")
    private Integer isShow;

    @ApiModelProperty(value = "颜色是否选中")
    private Integer isBgColor;

    @ApiModelProperty(value = "背景图是否选中")
    private Integer isBgPic;

    @ApiModelProperty(value = "背景颜色")
    private String colorPicker;

    @ApiModelProperty(value = "背景图")
    private String bgPic;

    @ApiModelProperty(value = "背景图图片样式")
    private Integer bgTabVal;

    @ApiModelProperty(value = "标题背景色")
    private String titleBgColor;

    @ApiModelProperty(value = "标题颜色")
    private String titleColor;

    @ApiModelProperty(value = "商户ID:0-非商户页面，>0-商户页面")
    private Integer merId;

    @ApiModelProperty(value = "默认模板(0-非首页，1.默认首页）")
    private Integer isDefault;
}
