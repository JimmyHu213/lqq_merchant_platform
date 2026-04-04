package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 系统表单响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemFormResponse", description="系统表单响应对象")
public class SystemFormResponse implements Serializable {

    private static final long serialVersionUID = -9090728897778197490L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "表单标题")
    private String formName;

    @ApiModelProperty(value = "表单内容")
    private String formValue;
}
