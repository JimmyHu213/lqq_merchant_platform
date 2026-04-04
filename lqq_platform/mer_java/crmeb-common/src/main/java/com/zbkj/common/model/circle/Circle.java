package com.zbkj.common.model.circle;

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
 * 圈层管理表
 * </p>
 *
 * @author zzp
 * @since 2025-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_circle")
@ApiModel(value="Circle对象", description="圈层管理表")
public class Circle implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "圈层ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "圈层名称")
    private String name;

    @ApiModelProperty(value = "父级ID")
    private Integer pid;

    @ApiModelProperty(value = "圈层层级 ：1-一级,2-二级,3-三级")
    private Integer level;

    @ApiModelProperty(value = "圈层地址")
    private String address;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "圈层范围")
    @TableField("`range`")
    private String range;

    @ApiModelProperty(value = "圈层地址开关：1=开，2-关")
    private Integer addressSwitch;

    @ApiModelProperty(value = "显示开关:0-关闭,1=开启")
    private Boolean isShow;

    @ApiModelProperty(value = "圈层开关:0-关闭,1=开启")
    private Boolean status;

    @ApiModelProperty(value = "是否删除：0-否，1-是")
    private Boolean isDelete;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "DIY模板id")
    private Integer diyId;
}
