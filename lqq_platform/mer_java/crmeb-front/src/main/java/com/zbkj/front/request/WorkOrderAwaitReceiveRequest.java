package com.zbkj.front.request;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 工单待领取查询请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FrontWorkClockInPageInfoResponse", description = "工单待领取查询请求对象")
public class WorkOrderAwaitReceiveRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = 7237850691096296451L;

    @ApiModelProperty(value = "关键字，工单号/订单号/商品名称")
    private String keywords;
}
