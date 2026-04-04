package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 锁客统计响应对象
 * [LQQ-迁移] 自动锁客
 */
@Data
@ApiModel(value = "LockCustomerCountResponse", description = "锁客统计响应对象")
public class LockCustomerCountResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "锁客总数")
    private Integer total;
}
