package com.zbkj.common.model.page;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * DIY数据表
 * </p>
 *
 * @author dazongzi
 * @since 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_page_diy")
@ApiModel(value="PageDiy对象", description="DIY数据表")
public class PageDiy implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "页面名称")
    private String name;

    @ApiModelProperty(value = "网站标题")
    private String title;

    @ApiModelProperty(value = "封面图")
    private String coverImage;

    @ApiModelProperty(value = "页面数据")
    private String value;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

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

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @ApiModelProperty(value = "标题背景色")
    private String titleBgColor;

    @ApiModelProperty(value = "标题颜色")
    private String titleColor;

    @ApiModelProperty(value = "商户ID:0-非商户页面，>0-商户页面")
    private Integer merId;

    @ApiModelProperty(value = "默认模板(0-非首页，1.默认首页）")
    private Integer isDefault;

    @TableField(exist = false)
    @ApiModelProperty(value = "圈层名称")
    private String circleNames;
}
