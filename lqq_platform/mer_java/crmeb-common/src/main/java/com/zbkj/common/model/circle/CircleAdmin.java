package com.zbkj.common.model.circle;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 圈层管理员表
 * </p>
 *
 * @author zzp
 * @since 2025-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_circle_admin")
@ApiModel(value="CircleAdmin对象", description="圈层管理员表")
public class CircleAdmin implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "管理员ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "管理员账号")
    private String account;

    @ApiModelProperty(value = "管理员密码")
    private String pwd;

    @ApiModelProperty(value = "管理员姓名")
    private String realName;

    @ApiModelProperty(value = "管理员头像")
    private String headerImage;

    @ApiModelProperty(value = "管理员角色(menus_id)")
    private String roles;

    @ApiModelProperty(value = "管理员最后一次登录ip")
    private String lastIp;

    @ApiModelProperty(value = "登录次数")
    private Integer loginCount;

    @ApiModelProperty(value = "管理员级别")
    private Integer level;

    @ApiModelProperty(value = "管理员状态 1有效，0无效")
    private Boolean status;

    @ApiModelProperty(value = "是否删除：1-删除")
    private Boolean isDel;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "是否接收短信")
    private Boolean isSms;

    @ApiModelProperty(value = "管理员类型：5=圈层超管，6=圈层管理员")
    private Integer type;

    @ApiModelProperty(value = "圈层id")
    private Integer circleId;

    @ApiModelProperty(value = "后台管理员添加时间")
    private Date createTime;

    @ApiModelProperty(value = "后台管理员最后一次登录时间")
    private Date updateTime;


}
