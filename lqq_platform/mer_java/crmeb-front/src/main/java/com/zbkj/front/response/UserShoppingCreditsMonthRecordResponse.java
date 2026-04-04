package com.zbkj.front.response;

import com.zbkj.common.model.user.UserShoppingCreditsRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户购物金月记录响应对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserShoppingCreditsMonthRecordResponse", description = "用户购物金月记录响应对象")
public class UserShoppingCreditsMonthRecordResponse implements Serializable {

    private static final long serialVersionUID = -3117517912723264620L;

    @ApiModelProperty(value = "推广年月")
    private String yearMonth;

    @ApiModelProperty(value = "推广订单信息")
    private List<UserShoppingCreditsRecord> recordList = new ArrayList<>();
}
