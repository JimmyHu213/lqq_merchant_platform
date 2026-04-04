package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 预约日期响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ReservationDateResponse", description = "预约日期响应对象")
public class ReservationDateResponse implements Serializable {

    private static final long serialVersionUID = 579424349538902267L;

    @ApiModelProperty(value = "日期：2025-09-29")
    private String date;

    @ApiModelProperty(value = "年月日的日")
    private Integer day;

    @ApiModelProperty(value = "周几")
    private Integer week;

}
