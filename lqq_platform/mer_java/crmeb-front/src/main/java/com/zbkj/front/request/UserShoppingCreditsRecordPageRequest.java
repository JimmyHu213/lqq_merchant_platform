package com.zbkj.front.request;

import com.zbkj.common.annotation.StringContains;
import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户购物金分页记录请求对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/4
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserShoppingCreditsRecordPageRequest", description = "用户购物金分页记录请求对象")
public class UserShoppingCreditsRecordPageRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = -1729545999037548699L;

    @ApiModelProperty(value = "商户ID")
    @NotNull(message = "商户ID不能为空")
    private Integer merId;

    @ApiModelProperty(value = "查询类型：all-全部，consume-消费，recharge-充值，refund-退款")
    @NotBlank(message = "查询类型不能为空")
    @StringContains(limitValues = {"all", "consume", "recharge", "refund"}, message = "未知的查询类型")
    private String searchType;
}
