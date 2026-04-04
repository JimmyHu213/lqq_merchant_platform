package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分销提现配置Vo对象
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/11/26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RetailStoreExtractConfigVo", description = "分销提现配置Vo对象")
public class RetailStoreExtractConfigVo implements Serializable {

    private static final long serialVersionUID = 1508423683637898026L;

    @ApiModelProperty(value = "分销提现方式：bankCard-银行卡，wechat-微信，alipay-支付宝，balance-余额，英文逗号分隔")
    private String retailStoreExtractMethod;

    @ApiModelProperty(value = "分销提现-微信到账方式：0-线下转账，1-商家转账到零钱")
    @NotNull(message = "微信到账方式 不能为空")
    @Range(min = 0, max = 1, message = "未知的微信到账方式")
    private Integer retailStoreExtractWechatMethod;

    @ApiModelProperty(value = "分销提现-微信转账场景ID")
    @NotNull(message = "微信转账场景ID 不能为空")
    private String retailStoreExtractWechatSceneId;

    @ApiModelProperty(value = "分销提现最低金额(元)")
    @NotNull(message = "分销提现最低金额 不能为空")
    @DecimalMin(value = "0", message = "分销提现最低金额最小为0")
    private BigDecimal retailStoreExtractMinPrice;

    @ApiModelProperty(value = "分销提现银行")
    @NotNull(message = "分销提现银行 不能为空")
    private String retailStoreExtractBank;

}
