package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="VerificationRecordResponse对象", description="核销记录表")
public class VerificationRecordResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "核销人ID")
    private Integer verifyStaffId;

    @ApiModelProperty(value = "核销人类型：1-商户端管理员，2-移动端商户管理员")
    private Integer verifyStaffType;

    @ApiModelProperty(value = "核销时间")
    private Date verifyTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "本次核销消耗的次数")
    private Integer verifyTimes;

    @ApiModelProperty(value = "核销人名字")
    private String verifyName;
}
