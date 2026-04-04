package com.zbkj.common.request;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 工单分页列表请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WorkOrderPageRequest", description = "工单分页列表请求对象")
public class WorkOrderPageRequest extends PageParamRequest implements Serializable {
    private static final long serialVersionUID = 5765158879296183376L;

    @ApiModelProperty(value = "工单状态：0-全部，2-待服务，3-进行中，4-已服务，9-已取消")
    @NotNull(message = "请选择工单状态")
    private Integer status;

    @ApiModelProperty(value = "关键字，工单号/订单号/商品名称")
    private String keywords;

    @ApiModelProperty(value = "员工ID，前端不传")
    private Integer staffId;

    @ApiModelProperty(value = "预约日期范围")
    private String dateLimit;
}
