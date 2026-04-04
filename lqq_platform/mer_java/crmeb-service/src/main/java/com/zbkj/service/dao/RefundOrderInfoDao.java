package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.order.RefundOrderInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 退款订单详情表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
public interface RefundOrderInfoDao extends BaseMapper<RefundOrderInfo> {

    /**
     * 获取退款商品数
     * @param date 日期
     * @param proId 商品ID
     * @return Integer
     */
    Integer getRefundProductNum(@Param("date") String date, @Param("proId")  Integer proId);

    Integer getRefundProductNumByDateAndMerId(@Param("date") String date, @Param("merId")  Integer merId);
}
