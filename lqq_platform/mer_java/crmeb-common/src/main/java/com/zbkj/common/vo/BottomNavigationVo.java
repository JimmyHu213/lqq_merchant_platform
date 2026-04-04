package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 底部导航VO对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BottomNavigationVo", description = "底部导航VO对象")
public class BottomNavigationVo implements Serializable {

    private static final long serialVersionUID = -4587230077716415074L;

    @ApiModelProperty(value = "选中图标链接地址")
    private String selectedIconLinkUrl;

    @ApiModelProperty(value = "未选中图标链接地址")
    private String unselectedIconLinkUrl;

    @ApiModelProperty(value = "导航名称")
    private String name;

    @ApiModelProperty(value = "跳转链接地址")
    private String linkUrl;

    @ApiModelProperty(value = "显示状态")
    private Boolean status;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}
