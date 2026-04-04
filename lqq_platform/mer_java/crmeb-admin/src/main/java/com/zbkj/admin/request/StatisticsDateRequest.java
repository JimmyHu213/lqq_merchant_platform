package com.zbkj.admin.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统计日期请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "StatisticsDateRequest", description = "统计日期请求对象")
public class StatisticsDateRequest implements Serializable {

    private static final long serialVersionUID = -1421462495847313377L;

    @ApiModelProperty(value = "时间格式：lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/")
    private String dateLimit;
}
