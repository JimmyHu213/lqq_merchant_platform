package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.order.MerchantOrder;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * <p>
 * 商户订单表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
public interface MerchantOrderDao extends BaseMapper<MerchantOrder> {

    List<MerchantOrder> selectMerchantOrderStatistics(Integer merId);

    Integer getOrderProductNumByDateAndMerId(@Param("date") String date,
                                             @Param("merId") Integer merId);

}
