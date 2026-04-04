package com.zbkj.common.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 通用时间搜索请求对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "DateRequest", description = "通用时间搜索请求对象")
public class DateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "开始时间")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "开始时间格式必须为yyyy-MM-dd，且日期必须有效"
    )
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "开始时间格式必须为yyyy-MM-dd，且日期必须有效"
    )
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "今天是否在时间段内", hidden = true)
    @JsonIgnore
    private Boolean isToday = Boolean.FALSE;
}
