package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.merchant.ShoppingCreditsOrder;
import com.zbkj.common.response.ShoppingCreditsOrderPageResponse;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 购物金订单 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2025-12-04
 */
public interface ShoppingCreditsOrderDao extends BaseMapper<ShoppingCreditsOrder> {

    /**
     * 商户端获取分页列表
     */
    List<ShoppingCreditsOrderPageResponse> findPageByMerchant(HashMap<String, Object> map);

    Integer getRechargeCountByMerIdAndDateGroupUserId(@Param(value = "merId") Integer merId,
                                                      @Param(value = "startDateTime") String startDateTime,
                                                      @Param(value = "endDateTime") String endDateTime);

    BigDecimal getRechargeAmountByMerIdAndDate(@Param(value = "merId") Integer merId,
                                               @Param(value = "startDateTime") String startDateTime,
                                               @Param(value = "endDateTime") String endDateTime);

    Integer getRechargeOrderNumByMerIdAndDate(@Param(value = "merId") Integer merId,
                                              @Param(value = "startDateTime") String startDateTime,
                                              @Param(value = "endDateTime") String endDateTime);
}
